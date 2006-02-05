package org.codehaus.xfire.aegis.type.xml;

import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.xml.sax.InputSource;

public class SourceTypeTest extends AbstractXFireAegisTest
{
    public void testSAX() throws Exception
    {
        InputSource is = new InputSource(getResourceAsStream("test.xml"));
        SAXSource s = new SAXSource(is);
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(bos);
        writer.writeStartDocument();
        
        SourceType st = new SourceType();
        st.writeObject(s, new ElementWriter(writer), new MessageContext());
        
        writer.writeEndDocument();
    }
    
    public void testStreamSource() throws Exception
    {
        StreamSource s = new StreamSource(getResourceAsStream("test.xml"));
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(bos);
        
        writer.writeStartDocument();
        
        SourceType st = new SourceType();
        st.writeObject(s, new ElementWriter(writer), new MessageContext());
        
        writer.writeEndDocument();
    }
}
