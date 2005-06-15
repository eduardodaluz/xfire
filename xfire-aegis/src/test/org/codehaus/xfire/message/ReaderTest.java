package org.codehaus.xfire.message;

import java.util.Date;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.yom.YOMReader;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.util.DateUtils;
import org.codehaus.yom.Document;
import org.codehaus.yom.stax.StaxBuilder;

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
        
        lr = getStreamReader("/org/codehaus/xfire/message/read1.xml");
        testReading2(lr);
    }

    private ElementReader getStreamReader(String resource)
        throws FactoryConfigurationError, XMLStreamException
    {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader( 
                getResourceAsStream(resource));
        
        while ( reader.getEventType() != XMLStreamReader.START_ELEMENT )
            reader.next();
        
        return new ElementReader(reader);
    }
    
    public void testDates() throws Exception
    {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader xr = factory.createXMLStreamReader( 
                getResourceAsStream( "/org/codehaus/xfire/message/dates.xml" ) );
        
        while ( xr.getEventType() != XMLStreamReader.START_ELEMENT )
            xr.next();
        
        MessageReader reader = new ElementReader(xr);
        
        Date date0 = DateUtils.parseDate(reader.getNextElementReader().getValue());
        assertNotNull(date0);
        Date dateTime0 =  DateUtils.parseDateTime(reader.getNextElementReader().getValue());
        assertNotNull(dateTime0);
        Date dateTime1 = DateUtils.parseDateTime(reader.getNextElementReader().getValue());
        assertNotNull(dateTime1);
        Date dateTime2 = DateUtils.parseDateTime(reader.getNextElementReader().getValue());
        assertNotNull(dateTime2);
        
        Date dateTime3 = DateUtils.parseDateTime(reader.getNextElementReader().getValue());
        assertNotNull(dateTime3);
        Date dateTime4 = DateUtils.parseDateTime(reader.getNextElementReader().getValue());
        assertNotNull(dateTime4);
        assertTrue ( dateTime3.before( dateTime4 ) );
        
        Date dateTime5 = DateUtils.parseDateTime(reader.getNextElementReader().getValue());
        assertNotNull(dateTime5);
        Date dateTime6 = DateUtils.parseDateTime(reader.getNextElementReader().getValue());
        assertNotNull(dateTime6);
        assertTrue ( dateTime5.before( dateTime6 ) );
                        
        //Date time0 = reader.getReader("time0").getValueAsTime();
    }
    
    public void testYOMReader()
        throws Exception
    {
        StaxBuilder builder = new StaxBuilder();
        Document doc = builder.build(getResourceAsReader("/org/codehaus/xfire/message/document/bean11.xml"));

        testReading(new YOMReader(doc.getRootElement()));
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
        
        MessageReader child = reader.getNextElementReader();
        assertEquals("child", child.getLocalName());
        assertTrue(child.hasMoreElementReaders());
    }
}
