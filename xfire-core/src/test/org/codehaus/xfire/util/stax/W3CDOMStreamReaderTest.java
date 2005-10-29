package org.codehaus.xfire.util.stax;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.util.DOMUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class W3CDOMStreamReaderTest
    extends AbstractStreamReaderTest
{
    
    
    public void testSingleElement() throws Exception
    {
        Document doc = getDocument();
        Element e = doc.createElementNS("urn:test","root");
        doc.appendChild(e);
        
        System.out.println("start: " + XMLStreamReader.START_ELEMENT);
        System.out.println("attr: " + XMLStreamReader.ATTRIBUTE);
        System.out.println("ns: " + XMLStreamReader.NAMESPACE);
        System.out.println("chars: " + XMLStreamReader.CHARACTERS);
        System.out.println("end: " + XMLStreamReader.END_ELEMENT);
        
        DOMUtils.writeXml(doc,System.out);
        W3CDOMStreamReader reader = new W3CDOMStreamReader(doc.getDocumentElement());
        testSingleElement(reader);
    }
    
    private Document getDocument() throws Exception{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document doc = factory.newDocumentBuilder().newDocument();
        return doc;
    }
    
    
    public void testTextChild() throws Exception
    {
        Document doc = getDocument();
        Element e = doc.createElementNS( "urn:test","root");
        doc.appendChild(e);
        Node text = doc.createTextNode("Hello World");
        e.appendChild(text);
        
        DOMUtils.writeXml(doc,System.out);
        
        W3CDOMStreamReader reader = new W3CDOMStreamReader(e);
        testTextChild(reader);
    }
    
    
    public void testAttributes() throws Exception
    {
        Document doc = getDocument();
        
        Element e = doc.createElementNS("urn:test","root");
        doc.appendChild(e);
        e.setAttribute("att1", "value1");
        Attr attr =doc.createAttributeNS("urn:test2","att2");
        attr.setValue("value2");
        attr.setPrefix("p");
        e.setAttributeNode(attr);
        DOMUtils.writeXml(doc,System.out);
        
        W3CDOMStreamReader reader = new W3CDOMStreamReader(doc.getDocumentElement());
        
        testAttributes(reader);
    }
    
    public void testElementChild() throws Exception
    {
        Document doc = getDocument();
        Element e = doc.createElementNS("urn:test","root");
        Element child =  doc.createElementNS("urn:test2","child");
        child.setPrefix("a");
        e.appendChild(child);
        doc.appendChild(e);
        DOMUtils.writeXml(doc,System.out);
        
        W3CDOMStreamReader reader = new W3CDOMStreamReader(e);
        testElementChild(reader);
    }
}
