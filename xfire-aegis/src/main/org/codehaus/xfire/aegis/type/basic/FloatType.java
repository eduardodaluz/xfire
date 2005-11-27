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
public class FloatType
    extends Type
{
    public void setTypeClass(Class typeClass)
    {
        super.setTypeClass(typeClass);
        
        if (typeClass.equals(Float.class))
        {
            setNillable(true);
        }
    }
    
    public Object readObject(MessageReader reader, MessageContext context)
    {
        return new Float( reader.getValueAsFloat() );
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
    {
        if (object instanceof Float)
        {
            writer.writeValueAsFloat((Float) object);
        }
        else
        {
            writer.writeValueAsFloat(new Float(((Number)object).floatValue()));
        }
    }
}
