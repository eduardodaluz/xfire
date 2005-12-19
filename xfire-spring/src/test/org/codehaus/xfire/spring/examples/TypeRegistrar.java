package org.codehaus.xfire.spring.examples;

import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
// START SNIPPET: registrar
public class TypeRegistrar
{
    private TypeMappingRegistry typeMappingRegistry;

    public void setTypeMappingRegistry(TypeMappingRegistry typeMappingRegistry)
    {
        this.typeMappingRegistry = typeMappingRegistry;
    }

    public void initializeTypes()
    {
        TypeMapping tm = typeMappingRegistry.getDefaultTypeMapping();
        tm.register(new CustomType());
    }
}
// END SNIPPET: registrar