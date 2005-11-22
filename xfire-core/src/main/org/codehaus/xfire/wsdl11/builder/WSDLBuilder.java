package org.codehaus.xfire.wsdl11.builder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl.SchemaType;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.jdom.Attribute;
import org.jdom.Element;

/**
 * WSDL
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WSDLBuilder
    extends org.codehaus.xfire.wsdl11.builder.AbstractWSDL
    implements WSDLWriter
{
    private PortType portType;

    private Map wsdlOps = new HashMap();

    private List declaredParameters = new ArrayList();

    public WSDLBuilder(Service service) throws WSDLException
    {
        super(service);
    }

    public void write(OutputStream out) throws IOException
    {
        try
        {
            PortType portType = createAbstractInterface();

            createConcreteInterface(portType);

            writeDocument();
        } catch (WSDLException e)
        {
            throw new XFireRuntimeException("error creating wsdl", e);
        }
        super.write(out);
    }

    public PortType createAbstractInterface()
        throws WSDLException
    {
        Service service = getService();
        Definition def = getDefinition();

        QName portName = service.getServiceInfo().getPortType();
        
        if (portName == null)
            portName = new QName(getTargetNamespace(), service.getName() + "PortType");

        portType = def.createPortType();
        portType.setQName(portName);
        portType.setUndefined(false);
        def.addPortType(portType);

        // Create Abstract operations
        for (Iterator itr = service.getServiceInfo().getOperations().iterator(); itr.hasNext();)
        {
            OperationInfo op = (OperationInfo) itr.next();

            // Create input message
            Message req = createInputMessage(op);
            def.addMessage(req);

            // Create output message if we have an out MEP
            Message res = null;
            if (op.getMEP().equals(SoapConstants.MEP_ROBUST_IN_OUT))
            {
                res = createOutputMessage(op);
                def.addMessage(res);
            }

            // Create the fault messages
            List faultMessages = new ArrayList();
            for (Iterator faultItr = op.getFaults().iterator(); faultItr.hasNext();)
            {
                FaultInfo fault = (FaultInfo) faultItr.next();
                Fault faultMsg = createFault(op, fault);
                faultMessages.add(faultMsg);
            }
            
            javax.wsdl.Operation wsdlOp = createOperation(op, req, res, faultMessages);
            wsdlOp.setUndefined(false);
            portType.addOperation(wsdlOp);

            wsdlOps.put(op.getName(), wsdlOp);
        }

        return portType;
    }

    public void createConcreteInterface(PortType portType)
    {
        Service service = getService();
        Definition def = getDefinition();

        QName name = service.getServiceInfo().getName();

        // Create a concrete instance for each transport.
        javax.wsdl.Service wsdlService = def.createService();
        wsdlService.setQName(name);

        for (Iterator itr = service.getBindings().iterator(); itr.hasNext();)
        {
            Binding binding = (Binding) itr.next();

            javax.wsdl.Binding wbinding = binding.createBinding(this, portType);
            
            Port port = binding.createPort(this, wbinding);
            if (port != null)
            {
                wsdlService.addPort(port);
            }
            
            // Add in user defined endpoints
            Collection endpoints = service.getEndpoints(binding.getName());
            if (endpoints == null) continue;
            
            for (Iterator eitr = endpoints.iterator(); eitr.hasNext();)
            {
                Endpoint ep = (Endpoint) eitr.next();

                port = binding.createPort(ep, this, wbinding);
                if (port != null)
                {
                    wsdlService.addPort(port);
                }
            }
        }
        
        def.addService(wsdlService);
    }

    private Message createOutputMessage(OperationInfo op)
    {
        // response message
        Message res = getDefinition().createMessage();
        res.setQName(new QName(getTargetNamespace(), op.getName() + "Response"));

        res.setUndefined(false);

        if (getService().getServiceInfo().isWrapped())
            createWrappedOutputParts(res, op);
        else
            createOutputParts(res, op);

        return res;
    }

    private Message createInputMessage(OperationInfo op)
    {
        Message req = getDefinition().createMessage();
        req.setQName(new QName(getTargetNamespace(), op.getName() + "Request"));
        req.setUndefined(false);

        if (getService().getServiceInfo().isWrapped())
            createWrappedInputParts(req, op);
        else
            createInputParts(req, op);

        return req;
    }

    private Fault createFault(OperationInfo op, FaultInfo faultInfo)
    {
        Message faultMsg = getDefinition().createMessage();
        faultMsg.setQName(new QName(getTargetNamespace(), faultInfo.getName()));
        faultMsg.setUndefined(false);
        getDefinition().addMessage(faultMsg);
        
        Fault fault = getDefinition().createFault();
        fault.setName(faultInfo.getName());
        fault.setMessage(faultMsg);

        for (Iterator itr = faultInfo.getMessageParts().iterator(); itr.hasNext();)
        {
            MessagePartInfo info = (MessagePartInfo) itr.next();
            
            Part part = createPart(info);
            faultMsg.addPart(part);
        }
        
        return fault;
    }

    public Part createPart(MessagePartInfo part)
    {
        return createPart(part.getName(), part.getTypeClass(), part.getSchemaType());
    }

    public Part createPart(QName pName, Class clazz, SchemaType type)
    {
        addDependency(type);

        QName schemaTypeName = type.getSchemaType();

        Part part = getDefinition().createPart();
        part.setName(pName.getLocalPart());

        if (!type.isAbstract())
        {
            String prefix = getNamespacePrefix(schemaTypeName.getNamespaceURI());
            addNamespace(prefix, schemaTypeName.getNamespaceURI());

            part.setElementName(schemaTypeName);

            return part;
        }

        if (!declaredParameters.contains(pName))
        {
            Element schemaEl = createSchemaType(getTargetNamespace());

            Element element = new Element("element", XSD_NS);
            schemaEl.addContent(element);

            String prefix = getNamespacePrefix(schemaTypeName.getNamespaceURI());
            addNamespace(prefix, schemaTypeName.getNamespaceURI());

            if (type.isAbstract())
            {
                element.setAttribute(new Attribute("name", pName.getLocalPart()));
                element.setAttribute(new Attribute("type",
                                                   prefix + ":" + schemaTypeName.getLocalPart()));
            }

            declaredParameters.add(pName);
        }

        part.setElementName(pName);

        return part;
    }

    public javax.wsdl.Operation createOperation(OperationInfo op, Message req, Message res, List faultMessages)
    {
        Definition def = getDefinition();
        javax.wsdl.Operation wsdlOp = def.createOperation();

        Input input = def.createInput();
        input.setMessage(req);
        input.setName(req.getQName().getLocalPart());
        wsdlOp.setInput(input);

        if (res != null)
        {
            Output output = def.createOutput();
            output.setMessage(res);
            output.setName(res.getQName().getLocalPart());
            wsdlOp.setOutput(output);
        }

        for (Iterator itr = faultMessages.iterator(); itr.hasNext();)
        {
            wsdlOp.addFault((Fault) itr.next());
        }
        
        wsdlOp.setName(op.getName());

        return wsdlOp;
    }
    
    public void createInputParts(Message req, OperationInfo op)
    {
        writeParameters(req, op.getInputMessage().getMessageParts());
    }

    public void createOutputParts(Message req, OperationInfo op)
    {
        writeParameters(req, op.getOutputMessage().getMessageParts());
    }

    private void writeParameters(Message message, Collection params)
    {
        for (Iterator itr = params.iterator(); itr.hasNext();)
        {
            MessagePartInfo param = (MessagePartInfo) itr.next();
            
            addNamespaceImport(getService().getServiceInfo().getName().getNamespaceURI(), 
                                       param.getSchemaType().getSchemaType().getNamespaceURI());
            
            Part part = createPart(param);
            
            message.addPart(part);
        }
    }

    protected void createWrappedInputParts(Message req, OperationInfo op)
    {
        Part part = getDefinition().createPart();

        QName typeQName = createDocumentType(op.getInputMessage(), 
                                             part,
                                             op.getName());
        part.setName("parameters");
        part.setElementName(typeQName);

        req.addPart(part);
    }

    protected void createWrappedOutputParts(Message req, OperationInfo op)
    {
        // response message part
        Part part = getDefinition().createPart();

        // Document style service
        QName typeQName = createDocumentType(op.getOutputMessage(), 
                                             part,
                                             op.getName() + "Response");
        part.setElementName(typeQName);
        part.setName("parameters");

        req.addPart(part);
    }

    protected QName createDocumentType(MessageInfo message, 
                                     Part part,
                                     String opName)
    {
        Element element = new Element("element", AbstractWSDL.XSD_NS);
        element.setAttribute(new Attribute("name", opName));

        Element complex = new Element("complexType", AbstractWSDL.XSD_NS);
        element.addContent(complex);

        if (message.getMessageParts().size() > 0)
        {
            Element sequence = createSequence(complex);

            writeParametersSchema(message.getMessageParts(), sequence);
        }

        /**
         * Don't create the schema until after we add the types in
         * (via WSDLBuilder.addDependency()) writeParametersSchema. 
         */
        Element schemaEl = createSchemaType(getTargetNamespace());
        schemaEl.addContent(element);

        return new QName(getTargetNamespace(), opName);
    }

    /**
     * @param op
     * @param sequence
     */
    protected void writeParametersSchema(Collection params, 
                                       Element sequence)
    {
        for (Iterator itr = params.iterator(); itr.hasNext();)
        {
            MessagePartInfo param = (MessagePartInfo) itr.next();

            QName pName = param.getName();
            SchemaType type = param.getSchemaType();

            addDependency(type);
            QName schemaType = type.getSchemaType();

            addNamespaceImport(getService().getServiceInfo().getName().getNamespaceURI(), 
                                       schemaType.getNamespaceURI());
            
            String uri = type.getSchemaType().getNamespaceURI();
            String prefix = getNamespacePrefix(uri);
            addNamespace(prefix, uri);

            Element element = new Element("element", AbstractWSDL.XSD_NS);
            sequence.addContent(element);

            if (type.isAbstract())
            {
                element.setAttribute(new Attribute("name", pName.getLocalPart()));
                
                element.setAttribute(new Attribute("type", 
                                                   prefix + ":" + schemaType.getLocalPart()));
            }
            else
            {
                element.setAttribute(new Attribute("ref",  prefix + ":" + schemaType.getLocalPart()));
            }

            element.setAttribute(new Attribute("minOccurs", "1"));
            element.setAttribute(new Attribute("maxOccurs", "1"));
        }
    }

    protected Element createSequence(Element complex)
    {
        Element sequence = new Element("sequence", AbstractWSDL.XSD_NS);
        complex.addContent(sequence);
        return sequence;
    }
}
