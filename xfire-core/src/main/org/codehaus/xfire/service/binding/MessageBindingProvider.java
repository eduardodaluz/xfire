package org.codehaus.xfire.service.binding;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.wsdl.SchemaType;
import org.codehaus.yom.Element;
import org.codehaus.yom.stax.StaxBuilder;
import org.codehaus.yom.stax.StaxSerializer;

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
        try
        {
            reader.next();
        }
        catch (XMLStreamException e1)
        {
            throw new XFireFault("Couldn't parse message.", e1, XFireFault.SENDER);
        }
        
        if (p.getTypeClass().isAssignableFrom(XMLStreamReader.class))
        {
            return context.getInMessage().getXMLStreamReader();
        }
        else if (p.getTypeClass().isAssignableFrom(Element.class))
        {
            StaxBuilder builder = new StaxBuilder();
            try
            {
                return builder.buildElement(null, context.getInMessage().getXMLStreamReader());
            }
            catch (XMLStreamException e)
            {
                throw new XFireFault("Couldn't parse stream.", e, XFireFault.SENDER);
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
            logger.warn("Unknown type for serialization: " + p.getTypeClass());
        }
    }

    public SchemaType getSchemaType(Service service, MessagePartInfo param)
    {
        return null;
    }
}
