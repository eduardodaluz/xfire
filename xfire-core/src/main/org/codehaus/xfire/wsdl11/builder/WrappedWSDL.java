package org.codehaus.xfire.wsdl11.builder;

import java.util.Collection;
import java.util.Iterator;

import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl.SchemaType;
import org.codehaus.yom.Attribute;
import org.codehaus.yom.Element;

/**
 * Creates Wrapped style WSDL documents.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WrappedWSDL
    extends DocumentWSDL
{
    public WrappedWSDL(ServiceEndpoint service, Collection transports) throws WSDLException
    {
        super(service, transports);
    }

    private QName createResponseDocumentType(OperationInfo op, Part part)
    {
        ServiceEndpoint service = getService();

        String opName = op.getName() + "Response";

        Element schemaEl = createSchemaType(getInfo().getTargetNamespace());
        Element element = new Element(elementQ, SoapConstants.XSD);
        schemaEl.appendChild(element);

        element.addAttribute(new Attribute("name", opName));

        Element complex = new Element(complexQ, SoapConstants.XSD);
        element.appendChild(complex);

        if (op.getOutputMessage().getMessageParts().size() > 0)
        {
            Element sequence = createSequence(complex);

            writeParametersSchema(op.getOutputMessage().getMessageParts(), sequence);
        }

        return new QName(getInfo().getTargetNamespace(), opName);
    }

    private QName createRequestDocumentType(OperationInfo op, Part part)
    {
        ServiceEndpoint service = getService();

        String opName = op.getName();// + "Request";

        Element schemaEl = createSchemaType(getInfo().getTargetNamespace());
        Element element = new Element(elementQ, SoapConstants.XSD);
        schemaEl.appendChild(element);

        element.addAttribute(new Attribute("name", opName));

        Element complex = new Element(complexQ, SoapConstants.XSD);
        element.appendChild(complex);

        if (op.getInputMessage().getMessageParts().size() > 0)
        {
            Element sequence = createSequence(complex);

            writeParametersSchema(op.getInputMessage().getMessageParts(), sequence);
        }

        return new QName(getInfo().getTargetNamespace(), opName);
    }

    /**
     * @param op
     * @param sequence
     */
    private void writeParametersSchema(Collection params, Element sequence)
    {
        ServiceEndpoint service = getService();

        for (Iterator itr = params.iterator(); itr.hasNext();)
        {
            MessagePartInfo param = (MessagePartInfo) itr.next();

            Class clazz = param.getTypeClass();
            QName pName = param.getName();
            SchemaType type = service.getBindingProvider().getSchemaType(service, param);

            addDependency(type);
            QName schemaType = type.getSchemaType();

            String uri = type.getSchemaType().getNamespaceURI();
            String prefix = getNamespacePrefix(uri);
            addNamespace(prefix, uri);

            Element outElement = new Element(elementQ, SoapConstants.XSD);
            sequence.appendChild(outElement);

            outElement.addAttribute(new Attribute("name", pName.getLocalPart()));
            outElement.addAttribute(new Attribute("type", prefix + ":"
                    + type.getSchemaType().getLocalPart()));

            outElement.addAttribute(new Attribute("minOccurs", "1"));
            outElement.addAttribute(new Attribute("maxOccurs", "1"));
        }
    }

    private Element createSequence(Element complex)
    {
        Element sequence = new Element(sequenceQ, SoapConstants.XSD);
        complex.appendChild(sequence);
        return sequence;
    }

    /**
     * @see org.codehaus.xfire.wsdl11.builder.AbstractJavaWSDL#createInputParts(javax.wsdl.Message,
     *      org.codehaus.xfire.java.OperationInfo)
     */
    protected void createInputParts(Message req, OperationInfo op)
    {
        Part part = getDefinition().createPart();

        QName typeQName = createRequestDocumentType(op, part);

        part.setName("parameters");
        part.setElementName(typeQName);

        req.addPart(part);
    }

    /**
     * @see org.codehaus.xfire.wsdl11.builder.AbstractJavaWSDL#createOutputParts(javax.wsdl.Message,
     *      org.codehaus.xfire.java.OperationInfo)
     */
    protected void createOutputParts(Message req, OperationInfo op)
    {
        // response message part
        Part part = getDefinition().createPart();

        // Document style service
        QName typeQName = createResponseDocumentType(op, part);
        part.setElementName(typeQName);
        part.setName("parameters");

        req.addPart(part);
    }
}
