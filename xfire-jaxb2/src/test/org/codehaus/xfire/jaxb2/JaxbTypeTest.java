package org.codehaus.xfire.jaxb2;

import junit.framework.TestCase;

import org.codehaus.xfire._enum.Countries;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeCreator;

import xfire.inheritance.BaseUser;

public class JaxbTypeTest extends TestCase
{
    public void testRegistry() {
        JaxbTypeRegistry registry = new JaxbTypeRegistry();
        
        TypeCreator creator = registry.getTypeCreator();
        
        Type type = creator.createType(BaseUser.class);
        assertTrue(type instanceof JaxbType);
        
        type = creator.createType(Countries.class);
        assertTrue(type instanceof JaxbType);
        
        assertEquals("Countries", type.getSchemaType().getLocalPart());
        assertEquals("http://xfire.codehaus.org/enum", 
                     type.getSchemaType().getNamespaceURI());
        assertTrue(type.isAbstract());
    }
}
