package org.codehaus.xfire.jaxb;

import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.TypeCreator;

import javax.xml.bind.JAXBContext;

/**
 * User: chris
 * Date: Aug 12, 2005
 * Time: 5:30:23 PM
 */
public class JaxbTypeRegistry
        extends DefaultTypeMappingRegistry
{
    private JAXBContext jaxbContext;

    public JaxbTypeRegistry(JAXBContext jaxbContext)
    {
        super(true);
        this.jaxbContext = jaxbContext;
    }

    protected TypeCreator createTypeCreator()
    {
        return new JaxbTypeCreator(super.createTypeCreator(), jaxbContext);
    }

}
