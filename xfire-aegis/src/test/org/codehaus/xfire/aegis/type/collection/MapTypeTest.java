package org.codehaus.xfire.aegis.type.collection;

import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeCreator;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.aegis.type.basic.BeanType;
import org.codehaus.xfire.aegis.type.basic.StringType;
import org.codehaus.xfire.aegis.yom.YOMWriter;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;

public class MapTypeTest
    extends AbstractXFireTest
{
    TypeMapping mapping;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        addNamespace("t", "urn:test");
        addNamespace("xsd", SoapConstants.XSD);
        addNamespace("xsi", SoapConstants.XSI_NS);
        
        TypeMappingRegistry reg = new DefaultTypeMappingRegistry(true);
        mapping = reg.createTypeMapping(true);
        mapping.setEncodingStyleURI("urn:test");
    }

    public void testBean()
        throws Exception
    {
        MapType type = new MapType(new QName("urn:test", "map"), String.class, String.class);
        type.setTypeClass(Map.class);
        type.setTypeMapping(mapping);
        
        assertNotNull(type.getSchemaType());
        assertEquals("entry", type.getEntryName().getLocalPart());
        assertEquals("key", type.getKeyName().getLocalPart());
        assertEquals("value", type.getValueName().getLocalPart());

        assertTrue(type.isComplex());
        
        Set deps = type.getDependencies();
        assertEquals(1, deps.size());
        Type stype = (Type) deps.iterator().next();
        assertTrue(stype instanceof StringType);
        
        // Test reading
        ElementReader reader = new ElementReader(getResourceAsStream("/org/codehaus/xfire/aegis/type/collection/Map.xml"));
        //MessageReader creader = reader.getNextElementReader();
        
        Map m = (Map) type.readObject(reader, new MessageContext());
        assertEquals(2, m.size());
        assertEquals("value1", m.get("key1"));
        assertEquals("value2", m.get("key2"));
        
        reader.getXMLStreamReader().close();
        
        // Test writing
        Element element = new Element("map", "urn:test");
        Document doc = new Document(element);
        YOMWriter writer = new YOMWriter(element);
        type.writeObject(m, writer, new MessageContext());
        writer.close();

        assertValid("/t:map/t:entry[1]/t:key[text()='key1']", element);
        assertValid("/t:map/t:entry[1]/t:value[text()='value1']", element);

        assertValid("/t:map/t:entry[2]/t:key[text()='key2']", element);
        assertValid("/t:map/t:entry[2]/t:value[text()='value2']", element);

        Element types = new Element("xsd:types", SoapConstants.XSD);
        Element schema = new Element("xsd:schema", SoapConstants.XSD);
        types.appendChild(schema);
        
        doc = new Document(types);
        
        type.writeSchema(schema);
        printNode(doc);
        assertValid("//xsd:complexType[@name='map']", doc);
        assertValid("//xsd:complexType[@name='map']/xsd:sequence/xsd:element[@name='entry']", doc);
        assertValid("//xsd:complexType[@name='map']/xsd:sequence/xsd:element[@name='entry']" +
                    "/xsd:complexType/xsd:sequence/xsd:element[@name='key'][@type='xsd:string']", doc);
        assertValid("//xsd:complexType[@name='map']/xsd:sequence/xsd:element[@name='entry']" +
                    "/xsd:complexType/xsd:sequence/xsd:element[@name='value'][@type='xsd:string']", doc);
    }
    
    public void testTypeCreator()
    {
        TypeCreator creator = mapping.getTypeCreator();

        BeanType beanType = (BeanType) creator.createType(MapBean.class);
        
        QName mapName = (QName) beanType.getTypeInfo().getElements().next();
        
        Type type = beanType.getTypeInfo().getType(mapName);
        assertTrue(type instanceof MapType);

        assertEquals(new QName(mapping.getEncodingStyleURI(), "string2stringMap"), type.getSchemaType());
        
        MapType mapType = (MapType) type;
        assertEquals(String.class, mapType.getValueClass());
        assertEquals(String.class, mapType.getKeyClass());
    }

}
