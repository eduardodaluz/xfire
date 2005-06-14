package org.codehaus.xfire.type.java5;

import java.lang.reflect.Method;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.type.CustomTypeMapping;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.collection.CollectionType;

public class CollectionTest
    extends AbstractXFireAegisTest
{
    private CustomTypeMapping tm;
    private Java5TypeCreator creator;

    public void setUp() throws Exception
    {
        super.setUp();
        
        tm = new CustomTypeMapping();
        creator = new Java5TypeCreator();
        tm.setTypeCreator(creator);
    }

    public void testType() throws Exception
    {
        Method m = CollectionService.class.getMethod("getStrings", new Class[0]);
        
        Type type = creator.createType(m, -1);
        tm.register(type);
        assertTrue( type instanceof CollectionType );
        
        CollectionType colType = (CollectionType) type;
        QName componentName = colType.getComponentName();
        
        type = colType.getComponentType();
        assertNotNull(type);
        assertTrue(type.getTypeClass().isAssignableFrom(String.class));
    }

    public class CollectionService
    {
        public Collection<String> getStrings() 
        {
            return null;
        }
        
        public void setLongs(Collection<Long> longs) 
        {
        }
    }
}
