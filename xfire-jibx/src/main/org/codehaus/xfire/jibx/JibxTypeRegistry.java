package org.codehaus.xfire.jibx;

import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.TypeCreator;
import org.codehaus.xfire.aegis.type.TypeMapping;

/**
 * @author tomeks
 *
 */
public class JibxTypeRegistry extends DefaultTypeMappingRegistry
{
    
    protected TypeMapping createTypeMapping(TypeMapping parent, boolean autoTypes)
    {
        TypeMapping tm = super.createTypeMapping(parent, autoTypes);
        
        createDefaultMappings(tm);
        
        return tm;
    }

    protected TypeCreator createTypeCreator() {
        JibxTypeCreator creator = new JibxTypeCreator();
        creator.setConfiguration(getConfiguration());

        return creator;
    }
}
