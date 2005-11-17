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
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.exchange.OutMessage;
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

public class WrappedBinding
    extends AbstractBinding
    implements WSDL11ParameterBinding, MessageSerializer
{
    public WrappedBinding()
    {
        setStyle(SoapConstants.STYLE_WRAPPED);
    }
    
    public void readMessage(InMessage inMessage, MessageContext context)
        throws XFireFault
    {
        Service endpoint = context.getService();
        
        List parameters = new ArrayList();
        DepthXMLStreamReader dr = new DepthXMLStreamReader(context.getInMessage().getXMLStreamReader());

        if ( !STAXUtils.toNextElement(dr) )
            throw new XFireFault("There must be a method name element.", XFireFault.SENDER);
        
        OperationInfo op = context.getExchange().getOperation();
        List operations = null;
        if (!isClientModeOn() && op == null)
        {
            operations = new ArrayList();
            for (Iterator itr = endpoint.getServiceInfo().getOperations().iterator(); itr.hasNext();)
            {
                OperationInfo candidate = (OperationInfo) itr.next();
                if (candidate.getName().equals(dr.getLocalName()))
                {
                    operations.add(candidate);
                }
            }
        }
        
        // Move from Operation element to whitespace or start element
        nextEvent(dr);
        
        read(inMessage, context, operations);
    }

    public void writeMessage(OutMessage message, XMLStreamWriter writer, MessageContext context)
        throws XFireFault
    {
        try
        {
            Service endpoint = context.getService();
            Object[] values = (Object[]) message.getBody();
            
            OperationInfo op = context.getExchange().getOperation();
            String name = null;
            
            MessageInfo msgInfo = null;
            if (isClientModeOn())
            {
                name = op.getName();
                msgInfo = op.getInputMessage();
            }
            else
            {
                name = op.getName() + "Response";
                msgInfo = op.getOutputMessage();
            }

            writeStartElement(writer, name, endpoint.getServiceInfo().getName().getNamespaceURI());
            
            int i = 0;
            for(Iterator itr = msgInfo.getMessageParts().iterator(); itr.hasNext();)
            {
                MessagePartInfo outParam = (MessagePartInfo) itr.next();
    
                getBindingProvider().writeParameter(outParam, writer, context, values[i]);
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

    public void createInputParts(WSDLBuilder builder,
                                 Message req, 
                                 OperationInfo op)
    {
        Part part = builder.getDefinition().createPart();

        QName typeQName = createDocumentType(builder, 
                                             op.getInputMessage(), 
                                             part,
                                             op.getName());
        part.setName("parameters");
        part.setElementName(typeQName);

        req.addPart(part);
    }

    public void createOutputParts(WSDLBuilder builder,
                                  Message req, 
                                  OperationInfo op)
    {
        // response message part
        Part part = builder.getDefinition().createPart();

        // Document style service
        QName typeQName = createDocumentType(builder, 
                                             op.getOutputMessage(), 
                                             part,
                                             op.getName() + "Response");
        part.setElementName(typeQName);
        part.setName("parameters");

        req.addPart(part);
    }

    private QName createDocumentType(WSDLBuilder builder,
                                     MessageInfo message, 
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

            writeParametersSchema(builder, message.getMessageParts(), sequence);
        }

        /**
         * Don't create the schema until after we add the types in
         * (via WSDLBuilder.addDependency()) writeParametersSchema. 
         */
        Element schemaEl = builder.createSchemaType(builder.getTargetNamespace());
        schemaEl.addContent(element);

        return new QName(builder.getTargetNamespace(), opName);
    }

    /**
     * @param op
     * @param sequence
     */
    private void writeParametersSchema(WSDLBuilder builder,
                                       Collection params, 
                                       Element sequence)
    {
        for (Iterator itr = params.iterator(); itr.hasNext();)
        {
            MessagePartInfo param = (MessagePartInfo) itr.next();

            QName pName = param.getName();
            SchemaType type = param.getSchemaType();

            builder.addDependency(type);
            QName schemaType = type.getSchemaType();

            builder.addNamespaceImport(builder.getService().getServiceInfo().getName().getNamespaceURI(), 
                                       schemaType.getNamespaceURI());
            
            String uri = type.getSchemaType().getNamespaceURI();
            String prefix = builder.getNamespacePrefix(uri);
            builder.addNamespace(prefix, uri);

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

    private Element createSequence(Element complex)
    {
        Element sequence = new Element("sequence", AbstractWSDL.XSD_NS);
        complex.addContent(sequence);
        return sequence;
    }

    public Object clone()
    {
        WrappedBinding binding = new WrappedBinding();
        binding.setBindingProvider(getBindingProvider());
        
        return binding;
    }    
}
