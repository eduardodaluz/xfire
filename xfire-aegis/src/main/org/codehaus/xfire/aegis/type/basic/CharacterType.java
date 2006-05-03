package org.codehaus.xfire.aegis.type.basic;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;

/**
 * @author <a href="mailto:struman@nuparadigm.com">Sean Truman</a>
 */
public class CharacterType extends Type
{    
    public Object readObject(MessageReader reader, MessageContext context)
    {
        return new Character( reader.getValueAsCharacter() );
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
    {
        writer.writeValueAsCharacter((Character)object);
    }
    
}