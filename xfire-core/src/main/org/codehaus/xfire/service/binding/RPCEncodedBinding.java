package org.codehaus.xfire.service.binding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.util.DepthXMLStreamReader;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.wsdl.SchemaType;
import org.codehaus.xfire.wsdl11.WSDL11ParameterBinding;
import org.codehaus.xfire.wsdl11.builder.AbstractWSDL;
import org.codehaus.yom.Attribute;
import org.codehaus.yom.Element;

public class RPCEncodedBinding
    extends WrappedBinding
    implements WSDL11ParameterBinding
{
    public RPCEncodedBinding()
    {
        setStyle(SoapConstants.STYLE_RPC);
        setUse(SoapConstants.USE_ENCODED);
    }

    public Object[] read(MessageContext context) throws XFireFault
    {
        ServiceEndpoint endpoint = context.getService();
        
        List parameters = new ArrayList();
        DepthXMLStreamReader dr = new DepthXMLStreamReader(context.getXMLStreamReader());
        
        if ( !STAXUtils.toNextElement(dr) )
            throw new XFireFault("There must be a method name element.", XFireFault.SENDER);
        
        String opName = dr.getLocalName();
        OperationInfo operation = endpoint.getService().getOperation( opName );
        if (operation == null)
        {
            // Determine the operation name which is in the form of:
            // xxxxRequest where xxxx is the operation.
            int index = opName.indexOf("Request");
            if (index > 0)
            {
                operation =endpoint.getService().getOperation( opName.substring(0, index) );
            }
        }
        
        context.setProperty(OPERATION_KEY, operation);

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

            parameters.add( getBindingProvider().readParameter(p, context) );
        }

        return parameters.toArray();
    }

    public void createInputParts(ServiceEndpoint endpoint, 
                                 AbstractWSDL wsdl,
                                 Message req, 
                                 OperationInfo op)
    {
        writeParametersSchema(endpoint, wsdl, req, op.getInputMessage());
    }

    public void createOutputParts(ServiceEndpoint endpoint, 
                                  AbstractWSDL wsdl,
                                  Message req, 
                                  OperationInfo op)
    {
        writeParametersSchema(endpoint, wsdl, req, op.getOutputMessage());
    }
    
    protected void writeParametersSchema(ServiceEndpoint service, 
                                         AbstractWSDL wsdl,
                                         Message message, 
                                         MessageInfo xmsg)
    {
        Collection params = xmsg.getMessageParts();
        
        for (Iterator itr = params.iterator(); itr.hasNext();)
        {
            MessagePartInfo param = (MessagePartInfo) itr.next();
            Class clazz = param.getTypeClass();
            QName pName = param.getName();

            SchemaType type = getBindingProvider().getSchemaType(service, param);
            wsdl.addDependency(type);
            QName schemaType = type.getSchemaType();

            Part part = wsdl.getDefinition().createPart();
            part.setName(pName.getLocalPart());

            if (type.isComplex())
            {
                part.setElementName(pName);

                Element schemaEl = wsdl.createSchemaType(wsdl.getInfo().getTargetNamespace());
                Element element = new Element(AbstractWSDL.elementQ, SoapConstants.XSD);
                schemaEl.appendChild(element);

                element.addAttribute(new Attribute("name", pName.getLocalPart()));

                String prefix = wsdl.getNamespacePrefix(schemaType.getNamespaceURI());
                wsdl.addNamespace(prefix, schemaType.getNamespaceURI());

                element.addAttribute(new Attribute("type", 
                                                   prefix + ":" + schemaType.getLocalPart()));
            }
            else
            {
                part.setElementName(type.getSchemaType());
            }

            message.addPart(part);
        }
    }
}
