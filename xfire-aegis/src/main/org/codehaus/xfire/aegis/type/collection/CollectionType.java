package org.codehaus.xfire.aegis.type.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.basic.ArrayType;
import org.codehaus.xfire.fault.XFireFault;

public class CollectionType
    extends ArrayType
{
    private Class componentType;
    
    public CollectionType(Class componentType)
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
            
            Collection values = createCollection();
            
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

    protected Collection createCollection()
    {
        Collection values = null;
        
        if (getTypeClass().isAssignableFrom(List.class))
        {
            values = new ArrayList();
        }
        else if (getTypeClass().isAssignableFrom(Set.class))
        {
            values = new HashSet();
        }
        else
        {
            values = new ArrayList();
        }
        
        return values;
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
        throws XFireFault
    {
        if (object == null)
            return;
    
        try
        {
            Collection list = (Collection) object;

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

                type.writeObject(itr.next(), cwriter, context);
                cwriter.close();
            }
        }
        catch (IllegalArgumentException e)
        {
            throw new XFireRuntimeException("Illegal argument.", e);
        }
    }    
    
    public Type getComponentType()
    {
        Type type = getTypeMapping().getType(componentType);
        
        if (type == null)
        {
            type = getTypeMapping().getTypeCreator().createType(componentType);
            getTypeMapping().register(type);
        }
        
        return type;
    }
}