package org.codehaus.xfire.jaxb2;

import javax.xml.bind.JAXBContext;

import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.TypeCreator;

public class JaxbTypeRegistry
    extends DefaultTypeMappingRegistry
{
    private JAXBContext jaxbContext;

    public JaxbTypeRegistry()
    {
        super(true);
    }

    public JaxbTypeRegistry(JAXBContext jaxbContext)
    {
        super(true);
        
        this.jaxbContext = jaxbContext;
    }
    
    public JaxbTypeRegistry(TypeCreator typeCreator, boolean createDefault)
    {
        super(typeCreator, createDefault);
    }

    protected TypeCreator createTypeCreator()
    {
        return new JaxbTypeCreator(super.createTypeCreator(), jaxbContext);
    }
}
