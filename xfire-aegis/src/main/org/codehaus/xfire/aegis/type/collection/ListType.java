package org.codehaus.xfire.aegis.type.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.basic.ArrayType;
import org.codehaus.xfire.fault.XFireFault;

public class ListType
    extends ArrayType
{
    private Class componentType;
    
    public ListType(Class componentType)
    {
        super();
        
        this.componentType = componentType;
    }
    
    
    public Object readObject(MessageReader reader, MessageContext context)
        throws XFireFault
    {
        try
        {
            Type compType = getComponentType();
            
            List values = new ArrayList();
            
            while (reader.hasMoreElementReaders())
            {
                MessageReader childReader = reader.getNextElementReader();
                
                values.add(compType.readObject(childReader, context));
            }
            
            return values;
        }
        catch (IllegalArgumentException e)
        {
            throw new XFireRuntimeException("Illegal argument.", e);
        }
    }
    public void writeObject(Object object, MessageWriter writer, MessageContext context)
        throws XFireFault
    {
        if (object == null)
            return;
    
        try
        {
            List list = (List) object;

            Type type = getComponentType();

            if (type == null)
                throw new XFireRuntimeException("Couldn't find type for " + type.getTypeClass() + ".");

            for (Iterator itr = list.iterator(); itr.hasNext();)
            {
                String ns = null;
                if (type.isAbstract())
                    ns = getSchemaType().getNamespaceURI();
                else
                    ns = type.getSchemaType().getNamespaceURI();

                MessageWriter cwriter = writer
                        .getElementWriter(type.getSchemaType().getLocalPart(), ns);

                type.writeObject(itr.next(), writer, context);
                cwriter.close();
            }
        }
        catch (IllegalArgumentException e)
        {
            throw new XFireRuntimeException("Illegal argument.", e);
        }
    }    
    
    protected Type getComponentType()
    {
        return getTypeMapping().getType(componentType);
    }
}