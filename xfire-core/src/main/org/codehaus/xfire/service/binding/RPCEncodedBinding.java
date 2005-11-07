package org.codehaus.xfire.service.binding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.util.stax.DepthXMLStreamReader;
import org.codehaus.xfire.wsdl.SchemaType;
import org.codehaus.xfire.wsdl11.WSDL11ParameterBinding;
import org.codehaus.xfire.wsdl11.builder.AbstractWSDL;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;
import org.jdom.Attribute;
import org.jdom.Element;

public class RPCEncodedBinding
    extends WrappedBinding
    implements WSDL11ParameterBinding
{
    public RPCEncodedBinding()
    {
        setStyle(SoapConstants.STYLE_RPC);
    }

    public void readMessage(InMessage inMessage, MessageContext context)
        throws XFireFault
    {
        Service endpoint = context.getService();
        
        List parameters = new ArrayList();
        DepthXMLStreamReader dr = new DepthXMLStreamReader(context.getInMessage().getXMLStreamReader());
        
        if ( !STAXUtils.toNextElement(dr) )
            throw new XFireFault("There must be a method name element.", XFireFault.SENDER);
        
        String opName = dr.getLocalName();
        OperationInfo operation = endpoint.getServiceInfo().getOperation( opName );
        if (operation == null)
        {
            // Determine the operation name which is in the form of:
            // xxxxRequest where xxxx is the operation.
            int index = opName.indexOf("Request");
            if (index > 0)
            {
                operation = endpoint.getServiceInfo().getOperation( opName.substring(0, index) );
            }
        }
        
        if (operation == null)
            throw new XFireFault("Could not find appropriate operation!", XFireFault.SENDER);
        
        // Move from operation element to whitespace or start element
        nextEvent(dr);
        
        setOperation(operation, context);

        if (operation == null)
        {
            throw new XFireFault("Invalid operation.", XFireFault.SENDER);
        }

        while(STAXUtils.toNextElement(dr))
        {
            MessagePartInfo p = operation.getInputMessage().getMessagePart(dr.getName());

            if (p == null)
            {
                throw new XFireFault("Parameter " + dr.getName() + " does not exist!", 
                                     XFireFault.SENDER);
            }

            parameters.add( getBindingProvider().readParameter(p, dr, context) );
        }
        
        context.getInMessage().setBody(parameters);
    }

    public void createInputParts(WSDLBuilder builder,
                                 Message req, 
                                 OperationInfo op)
    {
        writeParametersSchema(builder, req, op.getInputMessage());
    }

    public void createOutputParts(WSDLBuilder builder,
                                  Message req, 
                                  OperationInfo op)
    {
        writeParametersSchema(builder, req, op.getOutputMessage());
    }
    
    protected void writeParametersSchema(WSDLBuilder builder,
                                         Message message, 
                                         MessageInfo xmsg)
    {
        Collection params = xmsg.getMessageParts();
        
        for (Iterator itr = params.iterator(); itr.hasNext();)
        {
            MessagePartInfo param = (MessagePartInfo) itr.next();
            QName pName = param.getName();

            SchemaType type = param.getSchemaType();
            builder.addDependency(type);
            QName schemaType = type.getSchemaType();

            Part part = builder.getDefinition().createPart();
            part.setName(pName.getLocalPart());

            if (type.isComplex())
            {
                part.setElementName(pName);

                Element schemaEl = builder.createSchemaType(builder.getTargetNamespace());
                Element element = new Element("element", AbstractWSDL.XSD_NS);
                schemaEl.addContent(element);

                element.setAttribute(new Attribute("name", pName.getLocalPart()));

                String prefix = builder.getNamespacePrefix(schemaType.getNamespaceURI());
                builder.addNamespace(prefix, schemaType.getNamespaceURI());

                element.setAttribute(new Attribute("type", 
                                                   prefix + ":" + schemaType.getLocalPart()));
            }
            else
            {
                part.setElementName(type.getSchemaType());
            }

            message.addPart(part);
        }
    }

    public Object clone()
    {
        RPCEncodedBinding binding = new RPCEncodedBinding();
        binding.setBindingProvider(getBindingProvider());
        
        return binding;
    }    
}
