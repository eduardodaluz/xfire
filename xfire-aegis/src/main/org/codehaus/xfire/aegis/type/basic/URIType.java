package org.codehaus.xfire.aegis.type.basic;

import java.net.URI;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;

/**
 * <code>Type</code> for a <code>URI</code>
 *
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class URIType extends Type
{
    public Object readObject( final MessageReader reader, final MessageContext context )
    {
        final String value = reader.getValue();

        return null == value ? null : URI.create( value );
    }

    public void writeObject( final Object object, final MessageWriter writer, final MessageContext context )
    {
        writer.writeValue( ((URI)object).toASCIIString() );
    }
}
