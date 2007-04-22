package org.codehaus.xfire.aegis.type.java5;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;

import org.codehaus.xfire.aegis.type.Type;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * XMLTypeCreator with support for javax.xml.ws.Holder
 */
public class XMLTypeCreator extends
		org.codehaus.xfire.aegis.type.XMLTypeCreator {

	
	protected boolean isHolder(Class javaType)
    {
        return (javaType.equals(javax.xml.ws.Holder.class) || super.isHolder(javaType));
    }

    protected Type createHolderType(TypeClassInfo info)
    {
        Class heldCls = getComponentType(info.getGenericType(), 0);
        
        if( heldCls == null){
        	return super.createHolderType(info);
        }
        info.setTypeClass(heldCls);
        Type delegate = createTypeForClass(info);

        HolderType type = new HolderType(delegate);
        return type;
    }

    protected Class getComponentType(Object genericType, int index)
    {
        Class paramClass = null;

        if (genericType instanceof ParameterizedType)
        {
            ParameterizedType type = (ParameterizedType) genericType;

            if (type.getActualTypeArguments()[index] instanceof Class)
            {
                paramClass = (Class) type.getActualTypeArguments()[index];
            }

            else if (type.getActualTypeArguments()[index] instanceof WildcardType)
            {
                WildcardType wildcardType = (WildcardType) type.getActualTypeArguments()[index];

                if (wildcardType.getUpperBounds()[index] instanceof Class)
                {
                    paramClass = (Class) wildcardType.getUpperBounds()[index];
                }
            }
            else if (type.getActualTypeArguments()[index] instanceof ParameterizedType)
            {
                ParameterizedType ptype = (ParameterizedType) type.getActualTypeArguments()[index];
                paramClass = (Class) ptype.getRawType();
            }
        }
        return paramClass;
    }
}
