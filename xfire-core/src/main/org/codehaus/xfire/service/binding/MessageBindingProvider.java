package org.codehaus.xfire.service.binding;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.wsdl.SchemaType;
import org.codehaus.yom.Element;
import org.codehaus.yom.stax.StaxBuilder;

public class MessageBindingProvider
    implements BindingProvider
{
    private static final Log logger = LogFactory.getLog(MessageBindingProvider.class);
    
    public void initialize(Service newParam)
    {
    }

    public Object readParameter(Parameter p, MessageContext context)
        throws XFireFault
    {
        if (p.getTypeClass().isAssignableFrom(XMLStreamReader.class))
        {
            return context.getXMLStreamReader();
        }
        else if (p.getTypeClass().isAssignableFrom(Element.class))
        {
            StaxBuilder builder = new StaxBuilder();
            try
            {
                return builder.buildElement(null, context.getXMLStreamReader());
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

    public void writeParameter(Parameter p, MessageContext context, Object value)
        throws XFireFault
    {
        if (value instanceof Element)
        {
            
        }
        else
        {
            logger.warn("Unknown type for serialization: " + p.getTypeClass());
        }
    }

    public SchemaType getSchemaType(Service service, Parameter param)
    {
        return null;
    }
}
