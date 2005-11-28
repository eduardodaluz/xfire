package org.codehaus.xfire.wsdl11.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.SoapFaultSerializer;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.codehaus.xfire.wsdl.SchemaType;
import org.xml.sax.InputSource;

public class WSDLServiceBuilder
{
    private Service service;
    private ServiceInfo serviceInfo;
    private OperationInfo opInfo;
    private XmlSchemaCollection schemas;
    private List services = new ArrayList();
    private boolean isWrapped = false;
    private BindingProvider bindingProvider;
    
    protected final Definition definition;
    
    private List visitedPortTypes = new ArrayList();
    
    private List bindingAnnotators = new ArrayList();
    
    private Map wop2op = new HashMap();
    private Map winput2msg = new HashMap();
    private Map woutput2msg = new HashMap();
    
    private TransportManager transportManager =
        XFireFactory.newInstance().getXFire().getTransportManager();
    
    public WSDLServiceBuilder(Definition definition)
    {
        this.definition = definition;
        
        bindingAnnotators.add(new SoapBindingAnnotator());
    }

    public WSDLServiceBuilder(InputStream is) throws WSDLException
    {
        this(WSDLFactory.newInstance().newWSDLReader().readWSDL(null, new InputSource(is)));
    }

    public BindingProvider getBindingProvider()
    {
        if (bindingProvider == null)
        {
            try
            {
                bindingProvider = (BindingProvider) ClassLoaderUtils
                        .loadClass("org.codehaus.xfire.aegis.AegisBindingProvider", getClass()).newInstance();
            }
            catch (Exception e)
            {
                throw new XFireRuntimeException("Couldn't find a binding provider!", e);
            }
        }

        return bindingProvider;
    }
    
    public void setBindingProvider(BindingProvider bindingProvider)
    {
        this.bindingProvider = bindingProvider;
    }

    public Definition getDefinition()
    {
        return definition;
    }

    public TransportManager getTransportManager()
    {
        return transportManager;
    }

    public void setTransportManager(TransportManager transportManager)
    {
        this.transportManager = transportManager;
    }

    public void walkTree() throws Exception
    {
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

                if (!visitedPortTypes.contains(portType))
                {
                    visit(portType);
                    visitedPortTypes.add(portType);
                    
                    List operations = portType.getOperations();
                    for (int i = 0; i < operations.size(); i++)
                    {
                        Operation operation = (Operation) operations.get(i);
                        visit(operation);
                        {
                            Input input = operation.getInput();
                            visit(input);
                        }
                        {
                            Output output = operation.getOutput();
                            visit(output);
                        }
                        
                        Collection faults = operation.getFaults().values();
                        for (Iterator iterator2 = faults.iterator(); iterator2.hasNext();)
                        {
                            Fault fault = (Fault) iterator2.next();
                            //visit(fault);
                        }
                    }
                }
                
                visit(binding);
                
                visit(port);
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
        BindingAnnotator ann = getBindingAnnotator(binding);
        
        if (ann != null)
        {
            ann.visit(binding);
            
            List bindingOperations = binding.getBindingOperations();
            for (int i = 0; i < bindingOperations.size(); i++)
            {
                BindingOperation bindingOperation = 
                    (BindingOperation) bindingOperations.get(i);
                
                ann.visit(bindingOperation, (OperationInfo) wop2op.get(bindingOperation.getOperation()));
                
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
        }
    }

    protected BindingAnnotator getBindingAnnotator(Binding binding)
    {
        for (Iterator itr = bindingAnnotators.iterator(); itr.hasNext();)
        {
            BindingAnnotator ann = (BindingAnnotator) itr.next();
            if (ann.isUnderstood(binding))
            {
                ann.setService(service);
                return ann;
            }
        }
        
        return null;
    }
    
    protected void visit(BindingFault bindingFault, Fault fault)
    {
    }
    
    protected void visit(Fault fault)
    {
        FaultInfo faultInfo = opInfo.addFault(fault.getName());    
    }

    protected void visit(Input input)
    {
        MessageInfo info = opInfo.createMessage(input.getMessage().getQName());
        winput2msg.put(input, info);
        
        opInfo.setInputMessage(info);
        
        if (isWrapped)
        {
            createMessageParts(info, getWrappedSchema(input.getMessage()));
        }
        else
        {
            createMessageParts(info,  input.getMessage());
        }
    }

    protected void visit(Operation operation)
    {
        opInfo = serviceInfo.addOperation(operation.getName(), null);
        wop2op.put(operation, opInfo);

        isWrapped = isWrapped(operation);
    }
    
    protected void visit(BindingInput bindingInput, Input input)
    {
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

        SchemaType st = null;
        if (element.getRefName() != null)
        {
            st = getBindingProvider().getSchemaType(element.getRefName(), service);
        }
        else if (element.getSchemaTypeName() != null)
        {
            st = getBindingProvider().getSchemaType(element.getSchemaTypeName(), service);
        }
        
//        SimpleSchemaType st = new SimpleSchemaType();
//
//        if (element.getRefName() != null)
//        {
//            st.setAbstract(false);
//            st.setSchemaType(element.getRefName());
//        }
//        else
//        {
//            st.setAbstract(true);
//            st.setSchemaType(element.getSchemaTypeName());
//        }
        
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
    protected boolean isWrapped(Operation op)
    {
        Input input = op.getInput();
        Output output = op.getOutput();
        if (input.getMessage().getParts().size() != 1 || 
                output.getMessage().getParts().size() != 1) 
            return false;
        
        Part inPart = (Part) input.getMessage().getParts().values().iterator().next();
        Part outPart = (Part) output.getMessage().getParts().values().iterator().next();
        
        QName inElementName = inPart.getElementName();
        QName outElementName = outPart.getElementName();
        if (inElementName == null || outElementName == null) 
            return false;
        
        if (!inElementName.getLocalPart().equals(opInfo.getName()) || 
                !outElementName.getLocalPart().equals(opInfo.getName() + "Response")) 
            return false;
        
        XmlSchemaElement reqSchemaEl = schemas.getElementByQName(inElementName);
        XmlSchemaElement resSchemaEl = schemas.getElementByQName(outElementName);

        if (reqSchemaEl == null) 
            throw new XFireRuntimeException("Couldn't find schema for part: " + inElementName);

        // Now lets see if we have any attributes...
        // This should probably look at the restricted and substitute types too.
        if (reqSchemaEl.getSchemaType() instanceof XmlSchemaComplexType)
        {
            if (hasAttributes((XmlSchemaComplexType) reqSchemaEl.getSchemaType()))
                return false;
        }
        
        if (resSchemaEl.getSchemaType() instanceof XmlSchemaComplexType)
        {
            if (hasAttributes((XmlSchemaComplexType) resSchemaEl.getSchemaType()))
                return false;
        }

        return true;
    }
    
    
    private XmlSchemaComplexType getWrappedSchema(Message message)
    {
        Part part = (Part) message.getParts().values().iterator().next();
        
        XmlSchemaElement schemaEl = schemas.getElementByQName(part.getElementName());
        
        return (XmlSchemaComplexType) schemaEl.getSchemaType();
    }
    
    protected boolean hasAttributes(XmlSchemaComplexType complexType)
    {
        // Now lets see if we have any attributes...
        // This should probably look at the restricted and substitute types too.
        
        if (complexType.getAnyAttribute() != null ||
                complexType.getAttributes().getCount() > 0)
            return true;
        else
            return false;
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
                
                part.setSchemaType(getBindingProvider().getSchemaType(typeName, service));
            }

            // We've got a concrete schema type
            QName elementName = entry.getElementName();
            if (elementName != null)
            {
                MessagePartInfo part = info.addMessagePart(elementName, null);

                part.setSchemaType(getBindingProvider().getSchemaType(typeName, service));
            }
        }
    }

    protected String getTargetNamespace()
    {
        return getDefinition().getTargetNamespace();
    }

    protected void visit(BindingOutput bindingOutput, Output output)
    {
    }
    
    protected void visit(Output output)
    {
        MessageInfo info = opInfo.createMessage(output.getMessage().getQName());
        opInfo.setOutputMessage(info);
        woutput2msg.put(output, info);
        
        if (isWrapped)
        {
            createMessageParts(info, getWrappedSchema(output.getMessage()));
        }
        else
        {
            createMessageParts(info, output.getMessage());
        }
    }

    protected void visit(BindingOperation operation)
    {
        opInfo = serviceInfo.getOperation(operation.getName());
    }
    
    protected void begin(javax.wsdl.Service wservice)
    {
        serviceInfo = new ServiceInfo(wservice.getQName(), null, Object.class);
        
        service = new Service(serviceInfo);
    }

    protected void end(javax.wsdl.Service wservice)
    {
        serviceInfo.setWrapped(isWrapped);

        service.setFaultSerializer(new SoapFaultSerializer());
        service.setBindingProvider(getBindingProvider());
        services.add(service);
    }
    
    protected void visit(javax.wsdl.Port port)
    {
        BindingAnnotator ann = getBindingAnnotator(port.getBinding());
        if (ann != null)
        {
            ann.visit(port);
        }
    }
}