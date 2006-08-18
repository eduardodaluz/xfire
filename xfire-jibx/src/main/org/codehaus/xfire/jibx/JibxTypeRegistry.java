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
        JibxTypeCreator creator = new JibxTypeCreator();
        creator.setConfiguration(getConfiguration());
        return creator;
    }
}
