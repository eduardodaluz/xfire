package org.codehaus.xfire.aegis.type.collection;

import java.lang.reflect.Method;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.type.Configuration;
import org.codehaus.xfire.aegis.type.CustomTypeMapping;
import org.codehaus.xfire.aegis.type.DefaultTypeCreator;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.XMLTypeCreator;
import org.codehaus.xfire.aegis.type.basic.DoubleType;

public class CollectionTypeTest extends AbstractXFireAegisTest
{
    private CustomTypeMapping tm;
    private XMLTypeCreator creator;

    public void setUp() throws Exception
    {
        super.setUp();
        
        DefaultTypeMappingRegistry registry = new DefaultTypeMappingRegistry(true);
        TypeMapping defaultTM = registry.getDefaultTypeMapping();
        
        tm = new CustomTypeMapping(defaultTM);
        creator = new XMLTypeCreator();
        creator.setConfiguration(new Configuration());
        DefaultTypeCreator next = new DefaultTypeCreator();
        next.setConfiguration(new Configuration());
        creator.setNextCreator(next);
        tm.setTypeCreator(creator);
    }

    public void testListofLists() throws Exception
    {
        Method m = ListService.class.getMethod("getListofListofDoubles", new Class[0]);
        
        Type type = creator.createType(m, -1);
        tm.register(type);
        assertTrue( type instanceof CollectionType );
        
        CollectionType colType = (CollectionType) type;
        assertEquals("LotsOfDoubles", colType.getSchemaType().getLocalPart());
        
        type = colType.getComponentType();
        assertTrue( type instanceof CollectionType );
        assertEquals("SomeDoubles", type.getSchemaType().getLocalPart());
        
        colType = (CollectionType) type;
        Type componentType = colType.getComponentType();
        assertTrue(componentType instanceof DoubleType);
    }
}
