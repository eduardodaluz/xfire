package org.codehaus.xfire.aegis.type.basic;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;

/**
 * SimpleSerializer
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class BooleanType
    extends Type
{
    public void setTypeClass(Class typeClass)
    {
        super.setTypeClass(typeClass);
        
        if (typeClass.equals(Boolean.class))
        {
            setNillable(true);
        }
    }
    
    public Object readObject(MessageReader reader, MessageContext context)
    {
        return new Boolean( reader.getValueAsBoolean() );
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
    {
        writer.writeValueAsBoolean( ((Boolean) object).booleanValue() );
    }
}
