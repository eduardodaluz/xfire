package org.codehaus.xfire.util;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.AbstractXFireTest;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class STAXUtilsTest
    extends AbstractXFireTest
{
    public void testAmazonDoc() throws Exception
    {
        XMLInputFactory ifactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = ifactory.createXMLStreamReader(getClass().getResourceAsStream("amazon.xml"));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(out);
        
        writer.writeStartDocument();
        STAXUtils.copy(reader, writer);
        writer.writeEndDocument();
        
        writer.close();
        
        System.out.println(out.toString());
        
        SAXReader sax = new SAXReader();
        Document doc = sax.read( new StringReader(out.toString()) );
        
        addNamespace("a", "http://xml.amazon.com/AWSECommerceService/2004-08-01");
        assertValid("/a:ItemLookup", doc);
        assertValid("/a:ItemLookup/a:Request/a:IdType", doc);
    }
}
