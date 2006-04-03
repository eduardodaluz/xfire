package org.codehaus.xfire.jibx;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeCreator;
import org.codehaus.xfire.aegis.type.TypeMapping;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class JibxTypeCreator
    implements TypeCreator
{


    private TypeMapping typeMapping;

    /* (non-Javadoc)
     * @see org.codehaus.xfire.aegis.type.TypeCreator#createType(java.lang.reflect.Method, int)
     */
    public Type createType(Method method, int index)
    {
        Class clazz = null;
        if (index > -1)
        {
            clazz = method.getParameterTypes()[index];
        }
        else
        {
            clazz = method.getReturnType();
        }

        return new JibxType(clazz);

    }

    /* (non-Javadoc)
     * @see org.codehaus.xfire.aegis.type.TypeCreator#createType(java.beans.PropertyDescriptor)
     */
    public Type createType(PropertyDescriptor pd)
    {
        return new JibxType(pd.getPropertyType());
    }

    /* (non-Javadoc)
     * @see org.codehaus.xfire.aegis.type.TypeCreator#createType(java.lang.reflect.Field)
     */
    public Type createType(Field f)
    {
        return new JibxType(f.getType());
    }

    /* (non-Javadoc)
     * @see org.codehaus.xfire.aegis.type.TypeCreator#createType(java.lang.Class)
     */
    public Type createType(Class clazz)
    {
        return new JibxType(clazz);

    }

    /* (non-Javadoc)
     * @see org.codehaus.xfire.aegis.type.TypeCreator#setTypeMapping(org.codehaus.xfire.aegis.type.TypeMapping)
     */
    public void setTypeMapping(TypeMapping typeMapping)
    {
        this.typeMapping = typeMapping;

    }

}
