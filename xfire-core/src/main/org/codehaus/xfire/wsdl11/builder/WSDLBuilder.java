package org.codehaus.xfire.wsdl11.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.xml.namespace.QName;

import org.codehaus.xfire.service.MessageHeaderInfo;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.wsdl.SchemaType;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.xfire.wsdl11.WSDL11ParameterBinding;
import org.codehaus.xfire.wsdl11.WSDL11Transport;
import org.codehaus.yom.Attribute;
import org.codehaus.yom.Element;
import org.codehaus.yom.Elements;

import com.ibm.wsdl.extensions.soap.SOAPHeaderImpl;

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

    private Binding binding;

    private Collection transports;

    private Map wsdlOps = new HashMap();

    private WSDL11ParameterBinding paramBinding;

    private List declaredParameters = new ArrayList();
    
    public WSDLBuilder(Service service, 
                       Collection transports,
                       WSDL11ParameterBinding paramBinding) throws WSDLException
    {
        super(service);

        this.transports = transports;
        this.paramBinding = paramBinding;

        PortType portType = createAbstractInterface();

        createConcreteInterface(portType);

        writeDocument();
    }

    public PortType createAbstractInterface()
        throws WSDLException
    {
        Service service = getService();
        Definition def = getDefinition();

        QName portName = new QName(getInfo().getTargetNamespace(), getInfo().getPortType());

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

            javax.wsdl.Operation wsdlOp = createOperation(op, req, res);
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

        QName name = new QName(getInfo().getTargetNamespace(), getInfo().getServiceName());

        // Create a concrete instance for each transport.
        javax.wsdl.Service wsdlService = def.createService();
        wsdlService.setQName(name);

        for (Iterator itr = transports.iterator(); itr.hasNext();)
        {
            Object transportObj = (Transport) itr.next();

            if (!(transportObj instanceof WSDL11Transport))
            {
                continue;
            }
            
            WSDL11Transport transport = (WSDL11Transport) transportObj;

            Binding transportBinding = transport.createBinding(this, portType, paramBinding);

            for (Iterator oitr = service.getServiceInfo().getOperations().iterator(); oitr.hasNext();)
            {
                // todo: move out of the first loop, we'll be creating req/res
                // multiple times otherwise
                OperationInfo op = (OperationInfo) oitr.next();

                javax.wsdl.Operation wsdlOp = (javax.wsdl.Operation) wsdlOps.get(op.getName());

                BindingOperation bop = transport.createBindingOperation(this, portType, wsdlOp, paramBinding);
                transportBinding.addBindingOperation(bop);
                
                createHeaders(op, bop);
            }

            Port transportPort = transport.createPort(this, transportBinding);

            def.addBinding(transportBinding);
            wsdlService.addPort(transportPort);
        }

        def.addService(wsdlService);

    }

    private void createHeaders(OperationInfo op, BindingOperation bop)
    {
        List inputHeaders = op.getInputMessage().getMessageHeaders();
        if (inputHeaders.size() == 0)
        {
            return;
        }
        
        BindingInput bindingInput = bop.getBindingInput();
        
        Message reqHeaders = createHeaderMessages(op.getInputMessage());
        getDefinition().addMessage(reqHeaders);
       
        for (Iterator headerItr = reqHeaders.getParts().values().iterator(); headerItr.hasNext();)
        {
            Part headerInfo = (Part) headerItr.next();

            SOAPHeader soapHeader = new SOAPHeaderImpl();
            soapHeader.setMessage(reqHeaders.getQName());
            soapHeader.setPart(headerInfo.getName());
            soapHeader.setUse(paramBinding.getUse());
            
            bindingInput.addExtensibilityElement(soapHeader);
        }
    }

    private Message createOutputMessage(OperationInfo op)
    {
        // response message
        Message res = getDefinition().createMessage();
        res.setQName(new QName(getInfo().getTargetNamespace(), op.getName() + "Response"));

        res.setUndefined(false);

        paramBinding.createOutputParts(this, res, op);

        return res;
    }

    private Message createInputMessage(OperationInfo op)
    {
        Message req = getDefinition().createMessage();
        req.setQName(new QName(getInfo().getTargetNamespace(), op.getName() + "Request"));
        req.setUndefined(false);

        paramBinding.createInputParts(this, req, op);

        return req;
    }

    private Message createHeaderMessages(MessageInfo msgInfo)
    {
        Message msg = getDefinition().createMessage();

        msg.setQName(new QName(getInfo().getTargetNamespace(), msgInfo.getName().getLocalPart() + "Headers"));
        msg.setUndefined(false);

        for (Iterator itr = msgInfo.getMessageHeaders().iterator(); itr.hasNext();)
        {
            MessageHeaderInfo header = (MessageHeaderInfo) itr.next();
            
            Part part = createPart(header);

            msg.addPart(part);
        }
        
        return msg;
    }

    public Part createPart(MessageHeaderInfo header)
    {
        return createPart(header.getName(), header.getTypeClass(), header.getSchemaType());
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

        if (type.isComplex())
        {
            if (!declaredParameters.contains(pName))
            {
                Element schemaEl = createSchemaType(getInfo().getTargetNamespace());
                
                Element element = new Element(AbstractWSDL.elementQ, SoapConstants.XSD);
                schemaEl.appendChild(element);
    
                String prefix = getNamespacePrefix(schemaTypeName.getNamespaceURI());
                addNamespace(prefix, schemaTypeName.getNamespaceURI());
    
                if (type.isAbstract())
                {
                    element.addAttribute(new Attribute("name", pName.getLocalPart()));
                    element.addAttribute(new Attribute("type", 
                                                       prefix + ":" + schemaTypeName.getLocalPart()));
                }
                else
                {
                    element.addAttribute(new Attribute("ref",  prefix + ":" + schemaTypeName.getLocalPart()));
                }
                
                declaredParameters.add(pName);
            }
     
            part.setElementName(pName);
        }
        else
        {
            part.setTypeName(type.getSchemaType());
        }
        return part;
    }

    public javax.wsdl.Operation createOperation(OperationInfo op, Message req, Message res)
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

        wsdlOp.setName(op.getName());

        return wsdlOp;
    }
}
