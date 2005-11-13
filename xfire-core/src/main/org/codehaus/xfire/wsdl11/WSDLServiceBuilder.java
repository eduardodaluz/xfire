package org.codehaus.xfire.wsdl11;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Import;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.SoapFaultSerializer;
import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.binding.DocumentBinding;
import org.codehaus.xfire.service.binding.ObjectBindingFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.soap.SoapOperationInfo;
import org.codehaus.xfire.wsdl.SimpleSchemaType;
import org.xml.sax.InputSource;

public class WSDLServiceBuilder
{
    private Service service;
    private ServiceInfo serviceInfo;
    private OperationInfo opInfo;
    private XmlSchemaCollection schemas;
    private List services = new ArrayList();
    private String style;
    private boolean isWrapped = false;
    private XmlSchemaElement schema;

    protected final Definition definition;

    
    public WSDLServiceBuilder(Definition definition)
    {
        this.definition = definition;
    }

    public WSDLServiceBuilder(InputStream is) throws WSDLException
    {
        this(WSDLFactory.newInstance().newWSDLReader().readWSDL(null, new InputSource(is)));
    }

    public Definition getDefinition()
    {
        return definition;
    }
    

    public void walkTree() throws Exception
    {
        Exception e;
        
        //begin();

        //visit(definition);
        Collection imports = definition.getImports().values();
        for (Iterator iterator = imports.iterator(); iterator.hasNext();)
        {
            Import wsdlImport = (Import) iterator.next();
            //visit(wsdlImport);
        }
        visit(definition.getTypes());
        
        Collection messages = definition.getMessages().values();
        for (Iterator iterator = messages.iterator(); iterator.hasNext();)
        {
            Message message = (Message) iterator.next();
            //visit(message);
            Collection parts = message.getParts().values();
            for (Iterator iterator2 = parts.iterator(); iterator2.hasNext();)
            {
                Part part = (Part) iterator2.next();
                //visit(part);
            }
        }
        
        Collection services = definition.getServices().values();
        for (Iterator iterator = services.iterator(); iterator.hasNext();)
        {
            javax.wsdl.Service wservice = (javax.wsdl.Service) iterator.next();
            PortType portType = assertOnePortType(wservice);
            
            begin(wservice);
            
            Collection ports = wservice.getPorts().values();
            for (Iterator iterator1 = ports.iterator(); iterator1.hasNext();)
            {

                Port port = (Port) iterator1.next();
                Binding binding = port.getBinding();

                if (!isServiceUnderstood(wservice, port, binding))
                    continue;
                
                visit(port);
                visit(binding);
                
                List bindingOperations = binding.getBindingOperations();
                for (int i = 0; i < bindingOperations.size(); i++)
                {
                    BindingOperation bindingOperation = 
                        (BindingOperation) bindingOperations.get(i);
                    
                    visit(bindingOperation);
                    visit(bindingOperation.getBindingInput(), bindingOperation.getOperation().getInput());
                    visit(bindingOperation.getBindingOutput(), bindingOperation.getOperation().getOutput());
                    
                    Collection bindingFaults = bindingOperation.getBindingFaults().values();
                    for (Iterator iterator2 = bindingFaults.iterator(); iterator2.hasNext();)
                    {
                        BindingFault bindingFault = (BindingFault) iterator2.next();
                        Fault fault = bindingOperation.getOperation().getFault(bindingFault.getName());
                        
                        visit(bindingFault, fault);
                    }

                }
                
                visit(portType);
                
                List operations = portType.getOperations();
                for (int i = 0; i < operations.size(); i++)
                {
                    Operation operation = (Operation) operations.get(i);
                    //visit(operation);
                    {
                        Input input = operation.getInput();
                        //visit(input);
                    }
                    {
                        Output output = operation.getOutput();
                        //visit(output);
                    }
                    
                    Collection faults = operation.getFaults().values();
                    for (Iterator iterator2 = faults.iterator(); iterator2.hasNext();)
                    {
                        Fault fault = (Fault) iterator2.next();
                        //visit(fault);
                    }
                }
            }
            end(wservice);
        }

        //end();
    }
    
    private PortType assertOnePortType(javax.wsdl.Service wservice) throws XFireException
    {
        PortType portType = null;
    
        Collection ports = wservice.getPorts().values();
        for (Iterator iterator1 = ports.iterator(); iterator1.hasNext();)
        {
            Port port = (Port) iterator1.next();
            
            PortType newPT = port.getBinding().getPortType();
            
            if (portType == null) portType = newPT;
            
            if (newPT != portType)
                throw new XFireException("WSDLServiceBuilder can only handle one port type per service.");
        }
        
        return portType;
    }

    private boolean isServiceUnderstood(javax.wsdl.Service wservice, 
                                        Port port, 
                                        Binding binding)
    {
        if (DefinitionsHelper.getSOAPBinding(binding) != null)
            return true;
        
        return false;
    }

    public Collection getServices()
    {
        return services;
    }
    
    protected void visit(Types types)
    {
        schemas = new XmlSchemaCollection();

        for (Iterator itr = types.getExtensibilityElements().iterator(); itr.hasNext();)
        {
            ExtensibilityElement ee = (ExtensibilityElement) itr.next();
            
            if (ee instanceof UnknownExtensibilityElement)
            {
                UnknownExtensibilityElement uee = (UnknownExtensibilityElement) ee;
                schemas.read(uee.getElement());
            }
        }
    }
    
    protected void visit(PortType portType)
    {
        serviceInfo.setPortType(portType.getQName());
    }
    
    protected void visit(Binding binding)
    {
        SOAPBinding soapBinding = DefinitionsHelper.getSOAPBinding(binding);
    }

    protected void visit(BindingFault bindingFault, Fault fault)
    {
        FaultInfo faultInfo = opInfo.addFault(fault.getName());
        
        
    }

    protected void visit(BindingInput bindingInput, Input input)
    {
        MessageInfo info = opInfo.createMessage(input.getMessage().getQName());
        
        opInfo.setInputMessage(info);
        
        schema = getWrappedSchema(input);
        isWrapped = schema != null;
        if (isWrapped)
        {
            if (schema.getSchemaType() instanceof XmlSchemaComplexType)
            {
                createMessageParts(info, (XmlSchemaComplexType) schema.getSchemaType());
            }
        }
        else
        {
            createMessageParts(info,  input.getMessage());
        }
    }

    private void createMessageParts(MessageInfo info, XmlSchemaComplexType type)
    {
        if (type.getParticle() instanceof XmlSchemaSequence)
        {
            XmlSchemaSequence seq = (XmlSchemaSequence) type.getParticle();
            
            XmlSchemaObjectCollection col = seq.getItems();
            for (Iterator itr = col.getIterator(); itr.hasNext();)
            {
                XmlSchemaObject schemaObj = (XmlSchemaObject) itr.next();
                
                if (schemaObj instanceof XmlSchemaElement)
                {
                    createMessagePart(info,  (XmlSchemaElement) schemaObj);
                }
            }
        }
    }

    private void createMessagePart(MessageInfo info, XmlSchemaElement element)
    {
        MessagePartInfo part = info.addMessagePart(element.getQName(), XmlSchemaElement.class);
        
        SimpleSchemaType st = new SimpleSchemaType();

        if (element.getRefName() != null)
        {
            st.setAbstract(false);
            st.setSchemaType(element.getRefName());
        }
        else
        {
            st.setAbstract(true);
            st.setSchemaType(element.getSchemaTypeName());
        }
        
        part.setSchemaType(st);
    }

    /**
     * A message is wrapped IFF:
     * 
     * The input message has a single part. 
     * The part is an element. 
     * The element has the same name as the operation. 
     * The element's complex type has no attributes.
     * 
     * @return
     */
    protected XmlSchemaElement getWrappedSchema(Input input)
    {
        if (input.getMessage().getParts().size() != 1) 
            return null;
        
        Part part = (Part) input.getMessage().getParts().values().iterator().next();
        
        QName elementName = part.getElementName();
        if (elementName == null) 
            return null;
        
        if (!elementName.getLocalPart().equals(opInfo.getName())) 
            return null;
        
        XmlSchemaElement schemaEl = schemas.getElementByQName(elementName);
        
        if (schemaEl == null) 
            throw new XFireRuntimeException("Couldn't find schema for part: " + elementName);

        // Now lets see if we have any attributes...
        // This should probably look at the restricted and substitute types too.
        if (schemaEl.getSchemaType() instanceof XmlSchemaComplexType)
        {
            XmlSchemaComplexType complexType = (XmlSchemaComplexType) schemaEl.getSchemaType();
            
            if (complexType.getAnyAttribute() != null ||
                    complexType.getAttributes().getCount() > 0)
                return null;
            else
                return schemaEl;
        }
        
        return null;
    }
    
    private void createMessageParts(MessageInfo info, Message msg)
    {
        Map parts = msg.getParts();
        
        for (Iterator itr = parts.values().iterator(); itr.hasNext();)
        {
            Part entry = (Part) itr.next();
            
            // We're extending an abstract schema type
            QName typeName = entry.getTypeName();
            if (typeName != null)
            {
                QName partName = new QName(getTargetNamespace(), entry.getName());
                MessagePartInfo part = info.addMessagePart(typeName, null);
                
                SimpleSchemaType st = new SimpleSchemaType();
                st.setAbstract(true);
                
                part.setSchemaType(st);
            }

            // We've got a concrete schema type
            QName elementName = entry.getElementName();
            if (elementName != null)
            {
                MessagePartInfo part = info.addMessagePart(elementName, null);
                
                SimpleSchemaType st = new SimpleSchemaType();
                st.setAbstract(false);
                
                part.setSchemaType(st);
            }
        }
    }

    protected String getTargetNamespace()
    {
        return getDefinition().getTargetNamespace();
    }

    protected void visit(BindingOutput bindingOutput, Output output)
    {
        MessageInfo info = opInfo.createMessage(output.getMessage().getQName());
        
        opInfo.setOutputMessage(info);

        if (isWrapped)
        {
            if (schema.getSchemaType() instanceof XmlSchemaComplexType)
            {
                createMessageParts(info, (XmlSchemaComplexType) schema.getSchemaType());
            }
        }
        else
        {
            createMessageParts(info,  output.getMessage());
        }
    }

    protected void visit(BindingOperation operation)
    {
        opInfo = serviceInfo.addOperation(operation.getName(), null);
        
        String use = null;
        SOAPOperation soapOp = DefinitionsHelper.getSOAPOperation(operation);

        SOAPBody body = DefinitionsHelper.getSOAPBody(operation.getBindingInput().getExtensibilityElements());
        if (body != null)
        {
            use = body.getUse();
            
            String action = null;
            if (soapOp != null)
            {
                action = soapOp.getSoapActionURI();
                
                String opStyle = soapOp.getStyle();
                
                if (style != null && opStyle != null && !style.equals(opStyle))
                {
                    throw new XFireRuntimeException("Multiple styles are not supported yet!");
                }
                else if (soapOp.getStyle() != null)
                {
                    style = soapOp.getStyle();
                }
                else
                {
                    style = SoapConstants.STYLE_DOCUMENT;
                }                
            }

            new SoapOperationInfo(action, use, opInfo);
        }
    }
    
    protected void begin(javax.wsdl.Service wservice)
    {
        serviceInfo = new ServiceInfo(wservice.getQName(), null, Object.class);
        
        service = new Service(serviceInfo);
    }

    protected void end(javax.wsdl.Service wservice)
    {
        if (isWrapped)
        {
            style = "wrapped";
        }
        
        if (style != null)
            service.setBinding(ObjectBindingFactory.getMessageBinding(style, "literal"));
        else
            service.setBinding(new DocumentBinding());
        
        service.setFaultSerializer(new SoapFaultSerializer());
        
        services.add(service);
    }
    
    protected void visit(javax.wsdl.Port port)
    {
        SOAPAddress add = DefinitionsHelper.getSOAPAddress(port);
        SOAPBinding sbind = DefinitionsHelper.getSOAPBinding(port.getBinding());
        
        Endpoint ep = new Endpoint(new QName(getDefinition().getTargetNamespace(), port.getName()), 
                                   sbind.getTransportURI(), 
                                   add.getLocationURI());
        
        serviceInfo.addEndpoint(ep);
    }
}
