package org.codehaus.xfire.jaxb2;

import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.TypeCreator;

public class JaxbTypeRegistry
    extends DefaultTypeMappingRegistry
{
    public JaxbTypeRegistry()
    {
        super(true);
    }

    public JaxbTypeRegistry(TypeCreator typeCreator, boolean createDefault)
    {
        super(typeCreator, createDefault);
    }

    protected TypeCreator createTypeCreator()
    {
        return new JaxbTypeCreator(super.createTypeCreator());
    }
}
