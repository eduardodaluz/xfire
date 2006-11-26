package org.codehaus.xfire.aegis.stax;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.jdom.JDOMReader;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.jdom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 4, 2004
 */
public class ReaderTest
    extends AbstractXFireTest
{
    public void testLiteralReader()
        throws Exception
    {
        ElementReader lr = getStreamReader("/org/codehaus/xfire/message/document/bean11.xml");
        testReading(lr);
        
        lr = getStreamReader("/org/codehaus/xfire/aegis/stax/read1.xml");
        testReading2(lr);
        
        lr = getStreamReader("/org/codehaus/xfire/aegis/stax/read2.xml");
        testReading2(lr);
    }

    private ElementReader getStreamReader(String resource)
        throws FactoryConfigurationError, XMLStreamException
    {
        return new ElementReader(getResourceAsStream(resource));
    }
    
    public void testYOMReader()
        throws Exception
    {
        StaxBuilder builder = new StaxBuilder();
        Document doc = builder.build(getResourceAsReader("/org/codehaus/xfire/message/document/bean11.xml"));

        testReading(new JDOMReader(doc.getRootElement()));
    }
    
    public void testReading(MessageReader reader)
    {
        assertTrue(reader.getLocalName().equals("Envelope"));

        // make sure we can repeat this
        assertTrue(reader.hasMoreElementReaders());
        assertTrue(reader.hasMoreElementReaders());
        assertTrue(reader.hasMoreElementReaders());

        MessageReader header = reader.getNextElementReader();
        assertEquals("Header", header.getLocalName());
        assertEquals(Soap11.getInstance().getNamespace(), header.getNamespace());
        assertFalse(header.hasMoreElementReaders());

        MessageReader body = reader.getNextElementReader();
        assertEquals("Body", body.getLocalName());
        assertFalse(body.hasMoreElementReaders());
    }
    
    public void testReading2(MessageReader reader)
        throws Exception
    {
        assertEquals("test", reader.getLocalName());
        assertEquals("urn:test", reader.getNamespace());

        // make sure we can repeat this
        assertTrue(reader.hasMoreAttributeReaders());
        assertTrue(reader.hasMoreAttributeReaders());
        assertTrue(reader.hasMoreAttributeReaders());

        MessageReader one = reader.getNextAttributeReader();
        assertEquals("one", one.getValue());
        
        MessageReader two = reader.getNextAttributeReader();
        assertEquals("two", two.getValue());

        assertFalse(reader.hasMoreAttributeReaders());
        
        assertTrue(reader.hasMoreElementReaders());
        assertTrue(reader.hasMoreElementReaders());
        assertTrue(reader.hasMoreElementReaders());
        
        MessageReader foo = reader.getNextElementReader();
        assertEquals("bar", foo.getValue());
        
        MessageReader nil = reader.getNextElementReader();
        assertTrue(nil.isXsiNil());
        
        nil.readToEnd();
        
        assertTrue(reader.hasMoreElementReaders());
        MessageReader child = reader.getNextElementReader();
        assertEquals("child", child.getLocalName());
        assertTrue(child.hasMoreElementReaders());
        
        MessageReader gc1 = child.getNextElementReader();
        assertEquals("asdf", gc1.getValue());
        
        MessageReader gc2 = child.getNextElementReader();
        assertEquals("asdf", gc2.getValue());
        
        assertFalse(child.hasMoreElementReaders());
        assertTrue(reader.hasMoreElementReaders());
        
        foo = reader.getNextElementReader();
        assertEquals("test", foo.getValue());
        
        assertFalse(reader.hasMoreElementReaders());
    }
}
