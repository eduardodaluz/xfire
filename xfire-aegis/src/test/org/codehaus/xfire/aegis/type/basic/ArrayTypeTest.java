package org.codehaus.xfire.aegis.type.basic;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;

public class ArrayTypeTest
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
        ArrayType type = new ArrayType();
        type.setTypeClass(int[].class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:test", "ints"));

        // Test reading
        ElementReader reader = new ElementReader(getResourceAsStream("/org/codehaus/xfire/aegis/type/basic/ints1.xml"));
        
        int[] ints = (int[]) type.readObject(reader, new MessageContext());
        assertEquals(1, ints.length);
        
        reader.getXMLStreamReader().close();
        /*
        // Test writing
        Element element = new Element("t:ints", "urn:test");
        Document doc = new Document(element);
        type.writeObject(ints, new YOMWriter(element), new MessageContext());

        assertValid("/t:ints/t:int[text()='" + ints[0] +"']", element);
        */
    }
    
    public class PrimitiveArrayService
    {
        public int[] getInts() { return new int[] {0}; }
        public void setInts(int[] ints) {}
    }
}
