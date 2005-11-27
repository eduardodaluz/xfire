package org.codehaus.xfire.aegis.type.basic;

import java.util.Date;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.jdom.JDOMReader;
import org.codehaus.xfire.aegis.jdom.JDOMWriter;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;
import org.jdom.Element;

public class BeanTest
    extends AbstractXFireTest
{
    TypeMapping mapping;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        addNamespace("b", "urn:Bean");
        addNamespace("a", "urn:anotherns");
        addNamespace("xsd", SoapConstants.XSD);
        addNamespace("xsi", SoapConstants.XSI_NS);
        
        TypeMappingRegistry reg = new DefaultTypeMappingRegistry(true);
        mapping = reg.createTypeMapping(true);
    }

    public void testBean()
        throws Exception
    {
        BeanType type = new BeanType();
        type.setTypeClass(SimpleBean.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:Bean", "bean"));

        // Test reading
        ElementReader reader = new ElementReader(getResourceAsStream("/org/codehaus/xfire/aegis/type/basic/bean1.xml"));
        
        SimpleBean bean = (SimpleBean) type.readObject(reader, new MessageContext());
        assertEquals("bleh", bean.getBleh());
        assertEquals("howdy", bean.getHowdy());
        
        reader.getXMLStreamReader().close();
        
        // Test reading with extra elements
        reader = new ElementReader(getResourceAsStream("/org/codehaus/xfire/aegis/type/basic/bean2.xml"));        
        bean = (SimpleBean) type.readObject(reader, new MessageContext());
        assertEquals("bleh", bean.getBleh());
        assertEquals("howdy", bean.getHowdy());
        reader.getXMLStreamReader().close();
        
        // Test writing
        Element element = new Element("root", "b", "urn:Bean");
        Document doc = new Document(element);
        type.writeObject(bean, new JDOMWriter(element), new MessageContext());

        assertValid("/b:root/b:bleh[text()='bleh']", element);
        assertValid("/b:root/b:howdy[text()='howdy']", element);
    }
    
    public void testUnmappedProperty()
        throws Exception
    {
        BeanTypeInfo info = new BeanTypeInfo(SimpleBean.class, false);
        info.mapElement("howdy", "howdycustom");
        info.setTypeMapping(mapping);
        
        assertEquals("howdy", info.getPropertyDescriptorFromMappedName("howdycustom").getName());
        
        BeanType type = new BeanType(info);
        type.setTypeClass(SimpleBean.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:Bean", "bean"));
    
        ElementReader reader = new ElementReader(getResourceAsStream("/org/codehaus/xfire/aegis/type/basic/bean3.xml"));
        
        SimpleBean bean = (SimpleBean) type.readObject(reader, new MessageContext());
        assertEquals("howdy", bean.getHowdy());
        assertNull(bean.getBleh());
        
        reader.getXMLStreamReader().close();
        
        // Test writing
        Element element = new Element("root", "b", "urn:Bean");
        Document doc = new Document(element);
        type.writeObject(bean, new JDOMWriter(element), new MessageContext());

        assertInvalid("/b:root/b:bleh", element);
        assertValid("/b:root/b:howdycustom[text()='howdy']", element);
    }
    
    public void testAttributeMap()
        throws Exception
    {
        BeanTypeInfo info = new BeanTypeInfo(SimpleBean.class);
        info.mapAttribute("howdy", "howdy");
        info.mapAttribute("bleh", "bleh");
        info.setTypeMapping(mapping);
        
        BeanType type = new BeanType(info);
        type.setTypeClass(SimpleBean.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:Bean", "bean"));
    
        ElementReader reader = new ElementReader(getResourceAsStream("/org/codehaus/xfire/aegis/type/basic/bean4.xml"));
        
        SimpleBean bean = (SimpleBean) type.readObject(reader, new MessageContext());
        assertEquals("bleh", bean.getBleh());
        assertEquals("howdy", bean.getHowdy());

        reader.getXMLStreamReader().close();
        
        // Test writing
        Element element = new Element("root", "b", "urn:Bean");
        Document doc = new Document(element);
        type.writeObject(bean, new JDOMWriter(element), new MessageContext());

        assertValid("/b:root[@b:bleh='bleh']", element);
        assertValid("/b:root[@b:howdy='howdy']", element);
        
        Element types = new Element("types", "xsd", SoapConstants.XSD);
        Element schema = new Element("schema", "xsd", SoapConstants.XSD);
        types.addContent(schema);
        
        doc = new Document(types);
        
        type.writeSchema(schema);
        
        assertValid("//xsd:complexType[@name='bean']/xsd:attribute[@name='howdy']", schema);
        assertValid("//xsd:complexType[@name='bean']/xsd:attribute[@name='bleh']", schema);
    }
    
    public void testNullProperties()
        throws Exception
    {
        BeanTypeInfo info = new BeanTypeInfo(SimpleBean.class);
        info.setTypeMapping(mapping);
        info.mapAttribute("howdy", "howdy");
        info.mapElement("bleh", "bleh");
        
        BeanType type = new BeanType(info);
        type.setTypeClass(SimpleBean.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:Bean", "bean"));
    
        SimpleBean bean = new SimpleBean();
        
        // Test writing
        Element element = new Element("root", "b", "urn:Bean");
        Document doc = new Document(element);
        type.writeObject(bean, new JDOMWriter(element), new MessageContext());
    
        assertInvalid("/b:root[@b:howdy]", element);
        assertValid("/b:root/b:bleh[@xsi:nil='true']", element);
        
        Element types = new Element("types", "xsd", SoapConstants.XSD);
        Element schema = new Element("schema", "xsd", SoapConstants.XSD);
        types.addContent(schema);
        
        doc = new Document(types);
        
        type.writeSchema(schema);

        assertValid("//xsd:complexType[@name='bean']/xsd:attribute[@name='howdy']", schema);
        assertValid("//xsd:complexType[@name='bean']/xsd:sequence/xsd:element[@name='bleh']", schema);
    }
    
    public void testNillableInt()
        throws Exception
    {
        BeanTypeInfo info = new BeanTypeInfo(IntBean.class);
        info.setTypeMapping(mapping);
        
        BeanType type = new BeanType(info);
        type.setTypeClass(IntBean.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:Bean", "bean"));
        
        Element types = new Element("types", "xsd", SoapConstants.XSD);
        Element schema = new Element("schema", "xsd", SoapConstants.XSD);
        types.addContent(schema);
        
        Document doc = new Document(types);
        
        type.writeSchema(schema);

        assertValid("//xsd:complexType[@name='bean']/xsd:sequence/xsd:element[@name='int1'][@nillable='true']", schema);
        assertValid("//xsd:complexType[@name='bean']/xsd:sequence/xsd:element[@name='int2']", schema);
        assertInvalid("//xsd:complexType[@name='bean']/xsd:sequence/xsd:element[@name='int2'][@nillable='true']", schema);
    }   
    
    public void testNullNonNillableWithDate()
        throws Exception
    {
        BeanTypeInfo info = new BeanTypeInfo(DateBean.class);
        info.setTypeMapping(mapping);
        
        BeanType type = new BeanType(info);
        type.setTypeClass(DateBean.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:Bean", "bean"));
    
        DateBean bean = new DateBean();
        
        // Test writing
        Element element = new Element("root", "b", "urn:Bean");
        Document doc = new Document(element);
        type.writeObject(bean, new JDOMWriter(element), new MessageContext());

        // Make sure the date doesn't have an element. Its non nillable so it just
        // shouldn't be there.
        assertInvalid("/b:root/b:date", element);
        assertValid("/b:root", element);
    }
        
    /*
    public void testNonDefaultNames()
        throws Exception
    {
        BeanTypeInfo info = new BeanTypeInfo(SimpleBean.class);
        info.setTypeMapping(mapping);
        info.mapElement("howdy", new QName("urn:anotherns", "howdy"));
        info.mapElement("bleh", new QName("urn:anotherns", "bleh"));
        
        BeanType type = new BeanType(info);
        type.setTypeClass(SimpleBean.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:Bean", "bean"));
        
        // Test Reading
        ElementReader reader = new ElementReader(getResourceAsStream("/org/codehaus/xfire/aegis/type/basic/bean6.xml"));
        
        SimpleBean bean = (SimpleBean) type.readObject(reader, new MessageContext());
        assertEquals("bleh", bean.getBleh());
        assertEquals("howdy", bean.getHowdy());
        
        // Test writing
        Element element = new Element("b:root", "urn:Bean");
        Document doc = new Document(element);
        type.writeObject(bean, new JDOMWriter(element), new MessageContext());
    
        assertValid("/b:root/a:bleh[text()='bleh']", element);
        assertValid("/b:root/a:howdy[text()='howdy']", element);

        Element types = new Element("xsd:types", SoapConstants.XSD);
        Element schema = new Element("xsd:schema", SoapConstants.XSD);
        types.addContent(schema);
        
        doc = new Document(types);
        
        type.writeSchema(schema);

        // TODO: referenced types don't work yet
        //assertValid("//xsd:complexType[@name='bean']/xsd:element[@ref='ns1:howdy']", schema);
        //assertValid("//xsd:complexType[@name='bean']/xsd:sequence/xsd:element[@ref='ns1:bleh']", schema);
    }
    */
    
    public void testByteBean()
        throws Exception
    {
        BeanTypeInfo info = new BeanTypeInfo(ByteBean.class);
        info.setTypeMapping(mapping);
        
        BeanType type = new BeanType(info);
        type.setTypeClass(ByteBean.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:Bean", "bean"));
    
        Type dataType = type.getTypeInfo().getType("data");
        assertNotNull(dataType);
        
        assertTrue( type.getTypeInfo().isNillable("data") );
        
        ByteBean bean = new ByteBean();
        
        // Test writing
        Element element = new Element("root", "b", "urn:Bean");
        Document doc = new Document(element);
        type.writeObject(bean, new JDOMWriter(element), new MessageContext());
    
        // Make sure the date doesn't have an element. Its non nillable so it just
        // shouldn't be there.
        
        addNamespace("xsi", SoapConstants.XSI_NS);
        assertValid("/b:root/b:data[@xsi:nil='true']", element);

        bean = (ByteBean) type.readObject(new JDOMReader(element), new MessageContext());
        assertNotNull(bean);
        assertNull(bean.getData());
    }
        
    public void testGetSetRequired() throws Exception
    {
        BeanType type = new BeanType();
        type.setTypeClass(GoodBean.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:foo", "BadBean"));
        
        assertTrue(type.getTypeInfo().getElements().hasNext());
        
        type = new BeanType();
        type.setTypeClass(BadBean.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:foo", "BadBean"));
        
        assertFalse(type.getTypeInfo().getElements().hasNext());
        
        type = new BeanType();
        type.setTypeClass(BadBean2.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:foo", "BadBean2"));
        
        assertFalse(type.getTypeInfo().getElements().hasNext());
    }
    
    public static class DateBean
    {
        private Date date;

        public Date getDate() 
        {
            return date;
        }

        public void setDate(Date date) 
        {
            this.date = date;
        }
    }
    
    public static class IntBean
    {
        private Integer int1;
        private int int2;
        
        public Integer getInt1()
        {
            return int1;
        }
        public void setInt1(Integer int1)
        {
            this.int1 = int1;
        }
        public int getInt2()
        {
            return int2;
        }
        public void setInt2(int int2)
        {
            this.int2 = int2;
        }
    }

    public static class ByteBean
    {
        private byte[] data;

        public byte[] getData()
        {
            return data;
        }

        public void setData(byte[] data)
        {
            this.data = data;
        }
    }

    // This class only has a read property, no write
    public static class GoodBean
    {
        private String string;

        public String getString()
        {
            return string;
        }
    }
    
    public static class BadBean
    {
        public String delete()
        {
            return null;
        }
    }
    
    public static class BadBean2
    {
        public void setString(String string)
        {
        }
    }
}
