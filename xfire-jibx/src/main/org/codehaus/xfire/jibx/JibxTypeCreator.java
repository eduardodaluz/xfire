package org.codehaus.xfire.jibx;

import org.codehaus.xfire.aegis.type.DefaultTypeCreator;
import org.codehaus.xfire.aegis.type.Type;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class JibxTypeCreator
    extends DefaultTypeCreator
{
    /* (non-Javadoc)
     * @see org.codehaus.xfire.aegis.type.TypeCreator#createType(java.lang.Class)
     */
    public Type createDefaultType(Class clazz)
    {
        return new JibxType(clazz);

    }

    public Type createDefaultType(TypeClassInfo info)
    {
        JibxType type = new JibxType(info.getTypeClass());
        return type;
    }
    
    
}
