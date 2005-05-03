package org.codehaus.xfire.wsdl11.builder;

import java.util.Collection;
import java.util.Iterator;

import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl.SchemaType;
import org.codehaus.yom.Attribute;
import org.codehaus.yom.Element;

/**
 * Creates RPC/Encoded style WSDL documents for JavaServices.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @deprecated RPC/Encoded style is not (and probably never will be) supported by XFire.
 */
public class RPCEncodedWSDL
        extends AbstractJavaWSDL
{
    public RPCEncodedWSDL(ServiceEndpoint service, Collection transports)
            throws WSDLException
    {
        super(service, transports);
    }

    protected void createOutputParts(Message res, OperationInfo op)
    {
        writeParametersSchema(res, op.getOutputMessage());
    }

    protected void createInputParts(Message req, OperationInfo op)
    {
        writeParametersSchema(req, op.getInputMessage());
    }

    protected void writeParametersSchema(Message message, MessageInfo xmsg)
    {
        ServiceEndpoint service = getService();
        Collection params = xmsg.getMessageParts();
        
        for (Iterator itr = params.iterator(); itr.hasNext();)
        {
            MessagePartInfo param = (MessagePartInfo) itr.next();
            Class clazz = param.getTypeClass();
            QName pName = param.getName();

            SchemaType type = service.getBindingProvider().getSchemaType(service, param);
            addDependency(type);
            QName schemaType = type.getSchemaType();

            Part part = getDefinition().createPart();
            part.setName(pName.getLocalPart());

            if (type.isComplex())
            {
                part.setElementName(pName);

                Element schemaEl = createSchemaType(getInfo().getTargetNamespace());
                Element element = new Element(elementQ, SoapConstants.XSD);
                schemaEl.appendChild(element);

                element.addAttribute(new Attribute("name", pName.getLocalPart()));

                String prefix = getNamespacePrefix(schemaType.getNamespaceURI());
                addNamespace(prefix, schemaType.getNamespaceURI());

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
