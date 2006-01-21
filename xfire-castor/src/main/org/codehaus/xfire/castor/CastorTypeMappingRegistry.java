package org.codehaus.xfire.castor;

import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.TypeCreator;

/**
 * TypeMappingRegistry factory class for creating CastorTypeCreator classes.
 * 
 * @see org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry
 * @author Adam Kramer
 * @author Paul Saxman
 */
public class CastorTypeMappingRegistry
    extends DefaultTypeMappingRegistry
{

    private String file;

    public CastorTypeMappingRegistry()
    {
        super(true);
    }

    /**
     * Sets the name of the Castor mapping file to use for de/marshalling.
     * 
     * @param file
     *            The name of the Castor mapping file used for de/marshalling
     *            which presently needs to be in the classpath.
     */
    public void setMappingFile(String file)
    {
        this.file = file;
    }

    protected TypeCreator createTypeCreator()
    {
        CastorTypeCreator typeCreator = new CastorTypeCreator(super.createTypeCreator(), file);

        return typeCreator;
    }
}
