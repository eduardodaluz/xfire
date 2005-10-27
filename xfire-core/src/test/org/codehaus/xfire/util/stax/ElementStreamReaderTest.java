package org.codehaus.xfire.util.stax;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class ElementStreamReaderTest
    extends AbstractXFireTest
{
    public void testSingleElement() throws Exception
    {
        Element e = new Element("root", "urn:test");
        System.out.println("start: " + XMLStreamReader.START_ELEMENT);
        System.out.println("attr: " + XMLStreamReader.ATTRIBUTE);
        System.out.println("ns: " + XMLStreamReader.NAMESPACE);
        System.out.println("chars: " + XMLStreamReader.CHARACTERS);
        System.out.println("end: " + XMLStreamReader.END_ELEMENT);
        
        JDOMStreamReader reader = new JDOMStreamReader(e);
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.NAMESPACE, reader.next());
        
        assertEquals(1, reader.getNamespaceCount());
        assertEquals("", reader.getNamespacePrefix(0));
        assertEquals("urn:test", reader.getNamespaceURI(0));
        
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
    }
    
    public void testTextChild() throws Exception
    {
        Element e = new Element("root", "urn:test");
        e.addContent("Hello World");
        
        JDOMStreamReader reader = new JDOMStreamReader(e);
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.NAMESPACE, reader.next());
        
        assertEquals(1, reader.getNamespaceCount());
        assertEquals("", reader.getNamespacePrefix(0));
        assertEquals("urn:test", reader.getNamespaceURI(0));
        
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        
        assertEquals("Hello World", reader.getText());
        
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
    }

    public void testAttributes() throws Exception
    {
        Element e = new Element("root", "urn:test");
        e.setAttribute(new Attribute("att1", "value1"));
        e.setAttribute(new Attribute("att2",  "value2", Namespace.getNamespace("p", "urn:test2")));
        
        JDOMStreamReader reader = new JDOMStreamReader(e);
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.ATTRIBUTE, reader.next());
        
        // first attribute
        assertEquals(2, reader.getAttributeCount());
        assertEquals("", reader.getAttributePrefix(0));
        assertEquals("att1", reader.getAttributeLocalName(0));
        assertEquals("", reader.getAttributeNamespace(0));
        assertEquals("value1", reader.getAttributeValue(0));
        assertEquals("value1", reader.getAttributeValue("", "att1"));        
        
        QName q = reader.getAttributeName(0);
        assertEquals("", q.getNamespaceURI());
        assertEquals("", q.getPrefix());
        assertEquals("att1", q.getLocalPart());

        // second attribute
        assertEquals("p", reader.getAttributePrefix(1));
        assertEquals("att2", reader.getAttributeLocalName(1));
        assertEquals("urn:test2", reader.getAttributeNamespace(1));
        assertEquals("value2", reader.getAttributeValue(1));
        assertEquals("value2", reader.getAttributeValue("urn:test2", "att2"));        
        
        q = reader.getAttributeName(1);
        assertEquals("urn:test2", q.getNamespaceURI());
        assertEquals("p", q.getPrefix());
        assertEquals("att2", q.getLocalPart());

        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.ATTRIBUTE, reader.next());
        
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.NAMESPACE, reader.next());
        
        assertEquals(2, reader.getNamespaceCount());
        assertEquals("", reader.getNamespacePrefix(0));
        assertEquals("urn:test", reader.getNamespaceURI(0));
        assertEquals("p", reader.getNamespacePrefix(1));
        assertEquals("urn:test2", reader.getNamespaceURI(1));
        
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.NAMESPACE, reader.next());
        
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
    }
    
    public void testElementChild() throws Exception
    {
        Element e = new Element("root", "urn:test");
        Element child = new Element("child", "a", "urn:test2");
        e.addContent(child);
        
        JDOMStreamReader reader = new JDOMStreamReader(e);
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        
        assertEquals("root", reader.getLocalName());
        assertEquals("urn:test", reader.getNamespaceURI());
        assertEquals("", reader.getPrefix());
        
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.NAMESPACE, reader.next());
        
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        
        assertEquals("child", reader.getLocalName());
        assertEquals("urn:test2", reader.getNamespaceURI());
        assertEquals("a", reader.getPrefix());
        
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.NAMESPACE, reader.next());        
        
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        
        assertTrue(reader.hasNext());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
    }
}
