package org.codehaus.xfire.aegis.type.basic;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class StringType
    extends Type
{

    public StringType()
    {        
    }
    
    public Object readObject(MessageReader reader, MessageContext context)
    {
        return reader.getValue();
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
    {
        writer.writeValue( object );
    }
}
