package org.codehaus.xfire.jaxb;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.MessagePartInfo;

import javax.xml.stream.XMLStreamWriter;

/**
 * User: chris
 * Date: Aug 30, 2005
 * Time: 7:18:16 PM
 */
public class JaxbBindingProvider extends AegisBindingProvider
{
    public void writeParameter(MessagePartInfo p, XMLStreamWriter writer, MessageContext context, Object value) throws XFireFault
    {
        if (!JaxbTypeCreator.isJaxbType(p.getTypeClass()))
            super.writeParameter(p, writer, context, value);
        else
        {
            Type type = (Type) p.getSchemaType();

            MessageWriter mw;
            mw = new ElementWriter(writer);

            type.writeObject(value, mw, context);
        }

    }

    public JaxbBindingProvider(JaxbTypeRegistry jaxbTypeRegistry)
    {
        super(jaxbTypeRegistry);
    }
}
