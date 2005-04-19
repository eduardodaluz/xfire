package org.codehaus.xfire.wsdl11.builder;

import java.util.Collection;
import java.util.Iterator;

import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.codehaus.xfire.service.binding.ObjectService;
import org.codehaus.xfire.service.binding.Operation;
import org.codehaus.xfire.service.binding.Parameter;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl.SchemaType;
import org.codehaus.yom.Attribute;
import org.codehaus.yom.Element;

/**
 * Creates Document style WSDL documents for JavaServices.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DocumentWSDL
    extends AbstractJavaWSDL
{
    public DocumentWSDL(ObjectService service, Collection transports) throws WSDLException
    {
        super(service, transports);
    }

    protected void createOutputParts(Message res, Operation op)
    {
        writeParameters(res, op.getOutParameters());
    }

    protected void createInputParts(Message req, Operation op)
    {
        writeParameters(req, op.getInParameters());
    }

    /**
     * @param message
     * @param op
     * @param service
     */
    private void writeParameters(Message message, Collection params)
    {
        ObjectService service = (ObjectService) getService();

        for (Iterator itr = params.iterator(); itr.hasNext();)
        {
            Parameter param = (Parameter) itr.next();
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
