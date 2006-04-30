package org.codehaus.xfire.jibx;

import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.TypeCreator;

/**
 * @author tomeks
 *
 */
public class JibxTypeRegistry extends DefaultTypeMappingRegistry
{
    protected TypeCreator createTypeCreator(){
        return new JibxTypeCreator();
    }
}
