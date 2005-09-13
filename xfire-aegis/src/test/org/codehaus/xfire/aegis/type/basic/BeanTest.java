package org.codehaus.xfire.aegis.type.basic;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.aegis.type.basic.BeanType;
import org.codehaus.xfire.aegis.type.basic.BeanTypeInfo;
import org.codehaus.xfire.aegis.yom.YOMWriter;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;

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
        Element element = new Element("b:root", "urn:Bean");
        Document doc = new Document(element);
        type.writeObject(bean, new YOMWriter(element), new MessageContext());

        assertValid("/b:root/b:bleh[text()='bleh']", element);
        assertValid("/b:root/b:howdy[text()='howdy']", element);
    }
    
    public void testUnmappedProperty()
        throws Exception
    {
        BeanTypeInfo info = new BeanTypeInfo(SimpleBean.class, "urn:Bean");
        info.mapElement("howdy", new QName("urn:Bean", "howdycustom"));
        info.setTypeMapping(mapping);
        
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
        Element element = new Element("b:root", "urn:Bean");
        Document doc = new Document(element);
        type.writeObject(bean, new YOMWriter(element), new MessageContext());

        assertInvalid("/b:root/b:bleh", element);
        assertValid("/b:root/b:howdycustom[text()='howdy']", element);
    }
    
    public void testAttributeMap()
        throws Exception
    {
        BeanTypeInfo info = new BeanTypeInfo(SimpleBean.class, "urn:Bean");
        info.mapAttribute("howdy", new QName("urn:Bean", "howdy"));
        info.mapAttribute("bleh", new QName("urn:Bean", "bleh"));
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
        Element element = new Element("b:root", "urn:Bean");
        Document doc = new Document(element);
        type.writeObject(bean, new YOMWriter(element), new MessageContext());

        assertValid("/b:root[@b:bleh='bleh']", element);
        assertValid("/b:root[@b:howdy='howdy']", element);
        
        Element types = new Element("xsd:types", SoapConstants.XSD);
        Element schema = new Element("xsd:schema", SoapConstants.XSD);
        types.appendChild(schema);
        
        doc = new Document(types);
        
        type.writeSchema(schema);
        
        assertValid("//xsd:complexType[@name='bean']/xsd:attribute[@name='howdy']", schema);
        assertValid("//xsd:complexType[@name='bean']/xsd:attribute[@name='bleh']", schema);
    }
    
    public void testNullProperties()
        throws Exception
    {
        BeanTypeInfo info = new BeanTypeInfo(SimpleBean.class, "urn:Bean");
        info.setTypeMapping(mapping);
        info.mapAttribute("howdy", new QName("urn:Bean", "howdy"));
        info.mapElement("bleh", new QName("urn:Bean", "bleh"));
        
        BeanType type = new BeanType(info);
        type.setTypeClass(SimpleBean.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:Bean", "bean"));
    
        SimpleBean bean = new SimpleBean();
        
        // Test writing
        Element element = new Element("b:root", "urn:Bean");
        Document doc = new Document(element);
        type.writeObject(bean, new YOMWriter(element), new MessageContext());
    
        assertInvalid("/b:root[@b:howdy]", element);
        assertValid("/b:root/b:bleh[@xsi:nil='true']", element);
        
        Element types = new Element("xsd:types", SoapConstants.XSD);
        Element schema = new Element("xsd:schema", SoapConstants.XSD);
        types.appendChild(schema);
        
        doc = new Document(types);
        
        type.writeSchema(schema);

        assertValid("//xsd:complexType[@name='bean']/xsd:attribute[@name='howdy']", schema);
        assertValid("//xsd:complexType[@name='bean']/xsd:sequence/xsd:element[@name='bleh']", schema);
    }
    
    public void testNonDefaultNames()
        throws Exception
    {
        BeanTypeInfo info = new BeanTypeInfo(SimpleBean.class, "urn:Bean");
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
        type.writeObject(bean, new YOMWriter(element), new MessageContext());
    
        assertValid("/b:root/a:bleh[text()='bleh']", element);
        assertValid("/b:root/a:howdy[text()='howdy']", element);

        Element types = new Element("xsd:types", SoapConstants.XSD);
        Element schema = new Element("xsd:schema", SoapConstants.XSD);
        types.appendChild(schema);
        
        doc = new Document(types);
        
        type.writeSchema(schema);

        // TODO: referenced types don't work yet
        //assertValid("//xsd:complexType[@name='bean']/xsd:element[@ref='ns1:howdy']", schema);
        //assertValid("//xsd:complexType[@name='bean']/xsd:sequence/xsd:element[@ref='ns1:bleh']", schema);
    }
    
    public void testGetSetRequired() throws Exception
    {
        BeanType type = new BeanType();
        type.setTypeClass(BadBean.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:foo", "BadBean"));
        
        assertFalse(type.getTypeInfo().getElements().hasNext());
    }
    
    // This class only has a read property, no write
    public static class BadBean
    {
        private String string;

        public String getString()
        {
            return string;
        }
    }
}
