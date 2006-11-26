package org.codehaus.xfire.aegis.type.basic;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.test.AbstractXFireTest;

public class DynamicProxyTest
    extends AbstractXFireTest
{
    TypeMapping mapping;

    public void setUp()
        throws Exception
    {
        super.setUp();

        TypeMappingRegistry reg = new DefaultTypeMappingRegistry(true);
        mapping = reg.createTypeMapping(true);
    }

    public interface IMyInterface
    {
        public String getName();

        public void setName(String name);

        public boolean isUseless();

        public void setUseless(boolean useless);

        public String getNameById(int id);

        public void setNameNoParams();

        public void doSomething();

        public String get();

        public Integer set();

        public String getType();
        
        public String getFOO();
        
        public int getNonSpecifiedInt();
    }

    public interface IMyInterface2
    {
        public IMyInterface getMyInterface();
    }

    public void testDynamicProxy()
        throws Exception
    {
        BeanType type = new BeanType();
        type.setTypeClass(IMyInterface.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:MyInterface", "data"));

        ElementReader reader = new ElementReader(
                getResourceAsStream("/org/codehaus/xfire/aegis/type/basic/MyInterface.xml"));
        IMyInterface data = (IMyInterface) type.readObject(reader, new MessageContext());
        assertEquals("junk", data.getName());
        assertEquals(true, data.isUseless());
        data.setName("bigjunk");
        data.setUseless(false);
        assertEquals("bigjunk", data.getName());
        assertEquals(false, data.isUseless());

        assertTrue(data.hashCode() != 0);
        assertTrue(data.equals(data));
        assertFalse(data.equals(null));
        assertFalse(data.equals(new String("bigjunk")));
        assertNotNull(data.toString());
        
        assertEquals("foo", data.getFOO());
        
        assertEquals(0, data.getNonSpecifiedInt());
    }

    public void testDynamicProxyNonStandardGetter()
        throws Exception
    {
        BeanType type = new BeanType();
        type.setTypeClass(IMyInterface.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:MyInterface", "data"));

        ElementReader reader = new ElementReader(
                getResourceAsStream("/org/codehaus/xfire/aegis/type/basic/MyInterface.xml"));
        IMyInterface data = (IMyInterface) type.readObject(reader, new MessageContext());

        try
        {
            data.getNameById(0);
            fail(IllegalAccessError.class + " should be thrown.");
        }
        catch (IllegalAccessError e)
        {
        }

        try
        {
            data.get();
            fail(IllegalAccessError.class + " should be thrown.");
        }
        catch (IllegalAccessError e)
        {
        }
    }

    public void testDynamicProxyNonStandardSetter()
        throws Exception
    {
        BeanType type = new BeanType();
        type.setTypeClass(IMyInterface.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:MyInterface", "data"));

        ElementReader reader = new ElementReader(
                getResourceAsStream("/org/codehaus/xfire/aegis/type/basic/MyInterface.xml"));
        IMyInterface data = (IMyInterface) type.readObject(reader, new MessageContext());

        try
        {
            data.setNameNoParams();
            fail(IllegalAccessError.class + " should be thrown.");
        }
        catch (IllegalAccessError e)
        {
        }

        try
        {
            data.set();
            fail(IllegalAccessError.class + " should be thrown.");
        }
        catch (IllegalAccessError e)
        {
        }
    }

    public void testDynamicProxyNonGetterSetter()
        throws Exception
    {
        BeanType type = new BeanType();
        type.setTypeClass(IMyInterface.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:MyInterface", "data"));

        ElementReader reader = new ElementReader(
                getResourceAsStream("/org/codehaus/xfire/aegis/type/basic/MyInterface.xml"));
        IMyInterface data = (IMyInterface) type.readObject(reader, new MessageContext());

        try
        {
            data.doSomething();
            fail(IllegalAccessError.class + " should be thrown.");
        }
        catch (IllegalAccessError e)
        {
        }
    }

    public void testDynamicProxyMissingAttribute()
        throws Exception
    {
        BeanType type = new BeanType();
        type.setTypeClass(IMyInterface.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:MyInterface", "data"));

        ElementReader reader = new ElementReader(
                getResourceAsStream("/org/codehaus/xfire/aegis/type/basic/MyInterface.xml"));
        IMyInterface data = (IMyInterface) type.readObject(reader, new MessageContext());

        assertEquals("junk", data.getName());
        assertNull(data.getType());
    }

    public void testDynamicProxyNested()
        throws Exception
    {
        BeanType type = new BeanType();
        type.setTypeClass(IMyInterface.class);
        type.setSchemaType(new QName("urn:MyInterface", "myInterface"));
        type.setTypeMapping(mapping);
        BeanType type2 = new BeanType();
        type2.setTypeClass(IMyInterface2.class);
        type2.setSchemaType(new QName("urn:MyInterface2", "myInterface2"));
        type2.setTypeMapping(mapping);
        type2.getTypeInfo().mapType(new QName("urn:MyInterface", "myInterface"), type);

        ElementReader reader = new ElementReader(
                getResourceAsStream("/org/codehaus/xfire/aegis/type/basic/MyInterface2.xml"));
        IMyInterface2 data = (IMyInterface2) type2.readObject(reader, new MessageContext());

        assertNotNull(data.getMyInterface());
        assertEquals("junk", data.getMyInterface().getName());
        assertEquals(true, data.getMyInterface().isUseless());
    }
}
