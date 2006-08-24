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
    public Type createDefaultType(TypeClassInfo info)
    {
        JibxType type = new JibxType(info.getTypeClass());
        return type;
    }   
}
