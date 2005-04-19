package org.codehaus.xfire.aegis.type;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.type.basic.ArrayType;
import org.codehaus.xfire.aegis.type.basic.BeanType;
import org.codehaus.xfire.util.NamespaceHelper;

/**
 * A type mapping which automatically generates types
 * for java classes which are not registered, allowing
 * easy deployment of java services.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 21, 2004
 */
public class AutoTypeMapping
    extends CustomTypeMapping
{
    public AutoTypeMapping( TypeMapping defaultTM )
    {
        super( defaultTM );
    }
    
    public AutoTypeMapping()
    {
        super();
    }


    /**
     * Tries to determine a type class automatically from the type.
     * 
	 * @param javaType
	 * @return
	 */
	protected Type findTypeForClass(Class javaType)
	{
		if ( javaType.isArray() ||
             javaType.isAssignableFrom( Collection.class ) )
        {
			return new ArrayType();
        }
        else
        {
        	return new BeanType();
        }
	}

	/**
     * @see org.codehaus.xfire.aegis.type.TypeMapping#getType(java.lang.Class)
     */
    public Type getType(Class javaType)
    {
        Type type = super.getType(javaType);

        if ( type == null )
        {
            QName qname = createQName( javaType );
            type = findTypeForClass(javaType);
            
            type.setTypeClass(javaType);
            type.setSchemaType(qname);
            register(type);
        }
        
        return type;
    }

	/**
	 * @param javaType
	 * @return
	 * @throws ClassNotFoundException
	 */
	private QName createQName(Class javaType)
	{
        String clsName = javaType.getName();
        
        if (clsName.startsWith("[L"))
		{
			clsName = clsName.substring(2, clsName.length() - 1);
		}

        String ns = NamespaceHelper.makeNamespaceFromClassName(clsName, "http");
        
        String localName = null;
        
        if (javaType.isArray() ||
            javaType.isAssignableFrom( Collection.class ))
        {
            // If this is an array of a primitive type, put the type
            // we're creating in the default namespace.
            if ( javaType.isArray() )
            {
                Type type = getType( javaType.getComponentType() );
                
                if ( type.isComplex() )
                {
                    ns = type.getSchemaType().getNamespaceURI();
                }
                else
                {
                    ns = getEncodingStyleURI();
                }
                
                String first = type.getSchemaType().getLocalPart().substring(0,1);
                String last = type.getSchemaType().getLocalPart().substring(1);
                localName = "ArrayOf" + first.toUpperCase() + last;
            }
        }
        else
        {
            localName = clsName.substring( clsName.lastIndexOf(".")+1 );
        }

        return new QName( ns, localName );
	} 
}
