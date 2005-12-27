package org.codehaus.xfire.util;

import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.util.stax.DepthXMLStreamReader;
import org.jdom.Document;

import com.bea.xml.stream.MXParserFactory;
import com.bea.xml.stream.XMLOutputFactoryBase;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class STAXUtilsTest
    extends AbstractXFireTest
{
    private XMLInputFactory ifactory;
    private XMLOutputFactory ofactory;
    
    public void testWSTX() throws Exception
    {
        ifactory = new WstxInputFactory();
        ofactory = new WstxOutputFactory();

        doSkipTest();
        doNameSpaceDoc();
        doAmazonDoc();
        doEbayDoc();
        doAmazonDoc2();
        doDOMWrite();
        doDOMWrite2();
        doDOMRead();
    }
    
    public void testRI() throws Exception
    {
        ifactory = new MXParserFactory();
        ofactory = new XMLOutputFactoryBase();
        
        doSkipTest();
        doNameSpaceDoc();
        doAmazonDoc();
        doEbayDoc();
        doAmazonDoc2();
        doDOMWrite();
        doDOMWrite2();
        doDOMRead();
    }
    
    public void doSkipTest() throws Exception
    {
        XMLStreamReader reader = ifactory.createXMLStreamReader(getClass().getResourceAsStream("/org/codehaus/xfire/util/nowhitespace.xml"));
        
        DepthXMLStreamReader dr = new DepthXMLStreamReader(reader);
        STAXUtils.toNextElement(dr);
        assertEquals("Envelope", dr.getLocalName());
        dr.next();
        STAXUtils.toNextElement(dr);
        assertEquals("Header", dr.getLocalName());
    }
    
    public void doAmazonDoc() throws Exception
    {
        String outS = doCopy("amazon.xml");
        
        Document doc = readDocument(outS, ifactory);
        
        addNamespace("a", "http://xml.amazon.com/AWSECommerceService/2004-08-01");
        assertValid("/a:ItemLookup", doc);
        assertValid("/a:ItemLookup/a:Request/a:IdType", doc);
    }

    public void doEbayDoc() throws Exception
    {
        String outS = doCopy("ebay.xml");
        
        Document doc = readDocument(outS, ifactory);
        
        addNamespace("e", "urn:ebay:api:eBayAPI");
        addNamespace("ebase", "urn:ebay:apis:eBLBaseComponents");
        assertValid("//ebase:Version", doc);
        assertValid("//ebase:ErrorLanguage", doc);
        assertValid("//e:UserID", doc);
    }
    
    public void doAmazonDoc2() throws Exception
    {
        String outS = doCopy("amazon2.xml");
        
        Document doc = readDocument(outS, ifactory);
        
        addNamespace("a", "http://webservices.amazon.com/AWSECommerceService/2004-10-19");
        assertValid("//a:ItemLookupResponse", doc);
        assertValid("//a:ItemLookupResponse/a:Items", doc);
    }
    
    public void doNameSpaceDoc() throws Exception {
        String outS = doCopy("namespacedoc.xml");
        
        Document doc = readDocument(outS, ifactory);
        
        addNamespace("a", "http://www.paraware.com/2005/PriceAndAvailabilityCheckResponse");
        addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        assertValid("//a:Header", doc);
        assertValid("//a:Header/a:Reference/a:RefNum", doc);
        assertValid("//a:Body/a:PartNumbers/a:UPC/@xsi:nil", doc);
    }
    

    /**
     * @return
     * @throws FactoryConfigurationError
     * @throws XMLStreamException
     */
    private String doCopy(String resource) throws FactoryConfigurationError, XMLStreamException
    {
        XMLStreamReader reader = ifactory.createXMLStreamReader(getClass().getResourceAsStream(resource));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = ofactory.createXMLStreamWriter(out);
        
        writer.writeStartDocument();
        STAXUtils.copy(reader, writer);
        writer.writeEndDocument();
        
        writer.close();
        String outS = out.toString();
        
        return outS;
    }
    
    public void doDOMWrite() throws Exception
    {
        org.w3c.dom.Document doc = DOMUtils.readXml(getResourceAsStream("amazon.xml"));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLStreamWriter writer = ofactory.createXMLStreamWriter(bos);
        
        STAXUtils.writeElement(doc.getDocumentElement(), writer, false);
        
        writer.close();
        
        Document testDoc = readDocument(bos.toString(), ifactory);
        addNamespace("a", "http://xml.amazon.com/AWSECommerceService/2004-08-01");
        assertValid("//a:ItemLookup", testDoc);
        assertValid("//a:ItemLookup/a:Request", testDoc);
    }
    
    public void doDOMWrite2() throws Exception
    {
        org.w3c.dom.Document doc = DOMUtils.readXml(getResourceAsStream("nowhitespace.xml"));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLStreamWriter writer = ofactory.createXMLStreamWriter(bos);
        
        STAXUtils.writeElement(doc.getDocumentElement(), writer, false);
        
        writer.close();
        
        Document testDoc = readDocument(bos.toString(), ifactory);
    }
    
    public void doDOMRead() throws Exception
    {
        XMLStreamReader reader = ifactory.createXMLStreamReader(getResourceAsStream("amazon2.xml"));
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        org.w3c.dom.Document doc = STAXUtils.read(builder, reader);
        
        /*DOMUtils.writeXml(doc, System.out);
        DOMReader domReader = new DOMReader();
        Document testDoc = domReader.read(doc);

        addNamespace("a", "http://webservices.amazon.com/AWSECommerceService/2004-10-19");
        assertValid("//a:ItemLookupResponse", testDoc);
        assertValid("//a:ItemLookupResponse/a:Items", testDoc);
        assertValid("//a:OperationRequest/a:HTTPHeaders/a:Header[@Name='UserAgent']", testDoc);*/
    }
}
