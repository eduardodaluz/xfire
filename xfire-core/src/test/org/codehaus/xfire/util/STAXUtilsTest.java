package org.codehaus.xfire.util;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
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
        String outS = doCopy("amazon.xml");
        
        SAXReader sax = new SAXReader();
        Document doc = sax.read( new StringReader(outS) );
        
        addNamespace("a", "http://xml.amazon.com/AWSECommerceService/2004-08-01");
        assertValid("/a:ItemLookup", doc);
        assertValid("/a:ItemLookup/a:Request/a:IdType", doc);
    }

    public void testAmazonDoc2() throws Exception
    {
        String outS = doCopy("amazon2.xml");
        
        SAXReader sax = new SAXReader();
        Document doc = sax.read( new StringReader(outS) );
        
        addNamespace("a", "http://webservices.amazon.com/AWSECommerceService/2004-10-19");
        assertValid("//a:ItemLookupResponse", doc);
        assertValid("//a:ItemLookupResponse/a:Items", doc);
    }
    
    /**
     * @return
     * @throws FactoryConfigurationError
     * @throws XMLStreamException
     */
    private String doCopy(String resource) throws FactoryConfigurationError, XMLStreamException
    {
        XMLInputFactory ifactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = ifactory.createXMLStreamReader(getClass().getResourceAsStream(resource));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(out);
        
        writer.writeStartDocument();
        STAXUtils.copy(reader, writer);
        writer.writeEndDocument();
        
        writer.close();
        String outS = out.toString();
        
        System.out.println(outS);
        return outS;
    }
}
