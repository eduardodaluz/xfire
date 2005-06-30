package org.codehaus.xfire.xmlbeans;

import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.TypeCreator;

public class XmlBeansTypeRegistry
    extends DefaultTypeMappingRegistry
{
    protected TypeCreator createTypeCreator()
    {
        return new XmlBeansTypeCreator(super.createTypeCreator());
    }
}
