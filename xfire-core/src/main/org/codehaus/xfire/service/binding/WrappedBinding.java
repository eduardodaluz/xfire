package org.codehaus.xfire.service.binding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
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

public class WrappedBinding
    extends AbstractBinding
    implements WSDL11ParameterBinding
{
    public WrappedBinding()
    {
        setStyle(SoapConstants.STYLE_WRAPPED);
        setUse(SoapConstants.USE_LITERAL);
    }

    public Object[] read(MessageContext context) throws XFireFault
    {
        ServiceEndpoint endpoint = context.getService();
        
        List parameters = new ArrayList();
        DepthXMLStreamReader dr = new DepthXMLStreamReader(context.getXMLStreamReader());
        
        if ( !STAXUtils.toNextElement(dr) )
            throw new XFireFault("There must be a method name element.", XFireFault.SENDER);
        
        OperationInfo op = endpoint.getService().getOperation( dr.getLocalName() );
        
        if (op == null)
        {
            throw new XFireFault("Invalid operation.", XFireFault.SENDER);
        }
        context.setProperty(OPERATION_KEY, op);
        
        while(STAXUtils.toNextElement(dr))
        {
            MessagePartInfo p = op.getInputMessage().getMessagePart(dr.getName());

            if (p == null)
            {
                throw new XFireFault("Parameter " + dr.getName() + " does not exist!", 
                                     XFireFault.SENDER);
            }

            parameters.add( getBindingProvider().readParameter(p, context) );
        }
        
        return parameters.toArray();
    }

    public void write(Object[] values, MessageContext context) throws XFireFault
    {
        try
        {
            ServiceEndpoint endpoint = context.getService();
            XMLStreamWriter writer = (XMLStreamWriter) context.getProperty(AbstractHandler.STAX_WRITER_KEY);
            
            OperationInfo op = getOperation(context);
            String name = op.getName() + "Response";
            writeStartElement(writer, name, endpoint.getService().getName().getNamespaceURI());
            
            int i = 0;
            for(Iterator itr = op.getOutputMessage().getMessageParts().iterator(); itr.hasNext();)
            {
                MessagePartInfo outParam = (MessagePartInfo) itr.next();
    
                getBindingProvider().writeParameter(outParam, context, values[i]);
                i++;
            }
    
            writer.writeEndElement();
        }
        catch (XMLStreamException e)
        {
            throw new XFireRuntimeException("Couldn't write start element.", e);
        }
    }
    
    public void writeStartElement(XMLStreamWriter writer, String name, String namespace) 
        throws XMLStreamException
    {
        String prefix = "";
        
        writer.setPrefix(prefix, namespace);
        writer.writeStartElement(prefix, name, namespace);
        writer.writeNamespace(prefix, namespace);
    }

    public void createInputParts(ServiceEndpoint endpoint, 
                                 AbstractWSDL wsdl,
                                 Message req, 
                                 OperationInfo op)
    {
        Part part = wsdl.getDefinition().createPart();

        QName typeQName = createDocumentType(endpoint, 
                                             wsdl, 
                                             op.getInputMessage(), 
                                             part,
                                             op.getName());
        part.setName("parameters");
        part.setElementName(typeQName);

        req.addPart(part);
    }

    public void createOutputParts(ServiceEndpoint endpoint, 
                                  AbstractWSDL wsdl,
                                  Message req, 
                                  OperationInfo op)
    {
        // response message part
        Part part = wsdl.getDefinition().createPart();

        // Document style service
        QName typeQName = createDocumentType(endpoint, 
                                             wsdl, 
                                             op.getOutputMessage(), 
                                             part,
                                             op.getName() + "Response");
        part.setElementName(typeQName);
        part.setName("parameters");

        req.addPart(part);
    }
    
    private QName createDocumentType(ServiceEndpoint service, 
                                     AbstractWSDL wsdl,
                                     MessageInfo message, 
                                     Part part,
                                     String opName)
    {
        Element schemaEl = wsdl.createSchemaType(wsdl.getInfo().getTargetNamespace());
        Element element = new Element(AbstractWSDL.elementQ, SoapConstants.XSD);
        schemaEl.appendChild(element);

        element.addAttribute(new Attribute("name", opName));

        Element complex = new Element(AbstractWSDL.complexQ, SoapConstants.XSD);
        element.appendChild(complex);

        if (message.getMessageParts().size() > 0)
        {
            Element sequence = createSequence(complex);

            writeParametersSchema(service, wsdl, message.getMessageParts(), sequence);
        }

        return new QName(wsdl.getInfo().getTargetNamespace(), opName);
    }

    /**
     * @param op
     * @param sequence
     */
    private void writeParametersSchema(ServiceEndpoint service, 
                                       AbstractWSDL wsdl,
                                       Collection params, 
                                       Element sequence)
    {
        for (Iterator itr = params.iterator(); itr.hasNext();)
        {
            MessagePartInfo param = (MessagePartInfo) itr.next();

            Class clazz = param.getTypeClass();
            QName pName = param.getName();
            SchemaType type = getBindingProvider().getSchemaType(service, param);

            wsdl.addDependency(type);
            QName schemaType = type.getSchemaType();

            String uri = type.getSchemaType().getNamespaceURI();
            String prefix = wsdl.getNamespacePrefix(uri);
            wsdl.addNamespace(prefix, uri);

            Element outElement = new Element(AbstractWSDL.elementQ, SoapConstants.XSD);
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
        Element sequence = new Element(AbstractWSDL.sequenceQ, SoapConstants.XSD);
        complex.appendChild(sequence);
        return sequence;
    }
}
