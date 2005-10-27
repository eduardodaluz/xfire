package org.codehaus.xfire.service.binding;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.MessageHeaderInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.codehaus.xfire.util.jdom.StaxSerializer;
import org.codehaus.xfire.util.stax.FragmentStreamReader;
import org.codehaus.xfire.wsdl.SchemaType;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Namespace;
import org.w3c.dom.Document;

public class MessageBindingProvider
    implements BindingProvider
{
    private static final Log logger = LogFactory.getLog(MessageBindingProvider.class);
    
    public void initialize(Service newParam)
    {
    }

    public Object readParameter(MessagePartInfo p, XMLStreamReader reader, MessageContext context)
        throws XFireFault
    {
        if (p.getTypeClass().isAssignableFrom(XMLStreamReader.class))
        {
            return context.getInMessage().getXMLStreamReader();
        }
        else if (Element.class.isAssignableFrom(p.getTypeClass()))
        {
            StaxBuilder builder = new StaxBuilder();
            try
            {
                org.jdom.Document doc = builder.build(new FragmentStreamReader(context.getInMessage().getXMLStreamReader()));
                
                if (doc.hasRootElement())
                    return doc.getRootElement();
                else
                    return null;
            }
            catch (XMLStreamException e)
            {
                throw new XFireFault("Couldn't parse stream.", e, XFireFault.SENDER);
            }
        }
        else if (Document.class.isAssignableFrom(p.getTypeClass()))
        {
            try
            {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                return STAXUtils.read(builder, reader);
            }
            catch(Exception e)
            {
                throw new XFireFault("Couldn't read message.", e, XFireFault.SENDER);
            }
        }
        else if (p.getTypeClass().isAssignableFrom(MessageContext.class))
        {
            return context;
        }
        else
        {
            logger.warn("Unknown type for serialization: " + p.getTypeClass());
            return null;
        }
    }

    public void writeParameter(MessagePartInfo p, 
                               XMLStreamWriter writer,
                               MessageContext context, 
                               Object value)
        throws XFireFault
    {
        if (value == null) return;
        
        if (value instanceof Element)
        {
            StaxSerializer serializer = new StaxSerializer();
            try
            {
                serializer.writeElement((Element) value, writer);
            }
            catch (XMLStreamException e)
            {
                throw new XFireRuntimeException("Couldn't write to stream.", e);
            }
        }
        else
        {
            logger.warn("Unknown type for serialization: " + value.getClass());
        }
    }

    public QName getSuggestedName(Service service, OperationInfo op, int param)
    {
        return null;
    }

    public SchemaType getSchemaType(Service service, MessagePartInfo param)
    {
        return null;
    }

    public Object readHeader(MessageHeaderInfo p, MessageContext context)
        throws XFireFault
    {
        return context.getInMessage().getHeader().getChild(p.getName().getLocalPart(),
                                                           Namespace.getNamespace(p.getName().getNamespaceURI()));
    }

    public void writeHeader(MessagePartInfo p, MessageContext context, Object value)
        throws XFireFault
    {
        context.getOutMessage().getHeader().addContent((Content) value);
    }
}
