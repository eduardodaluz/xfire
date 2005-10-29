package org.codehaus.xfire.util.stax;

import javax.xml.stream.XMLStreamReader;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.DOMOutputter;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class W3CDOMStreamReaderTest
    extends AbstractStreamReaderTest
{
    DOMOutputter dom = new DOMOutputter();
    
    public void testSingleElement() throws Exception
    {
        Element e = new Element("root", "urn:test");
        Document doc = new Document(e);
        
        System.out.println("start: " + XMLStreamReader.START_ELEMENT);
        System.out.println("attr: " + XMLStreamReader.ATTRIBUTE);
        System.out.println("ns: " + XMLStreamReader.NAMESPACE);
        System.out.println("chars: " + XMLStreamReader.CHARACTERS);
        System.out.println("end: " + XMLStreamReader.END_ELEMENT);
        
        W3CDOMStreamReader reader = new W3CDOMStreamReader(dom.output(doc).getDocumentElement());
        testSingleElement(reader);
    }
    
    public void testTextChild() throws Exception
    {
        Element e = new Element("root", "urn:test");
        e.addContent("Hello World");
        Document doc = new Document(e);
        
        W3CDOMStreamReader reader = new W3CDOMStreamReader(dom.output(doc).getDocumentElement());
        testTextChild(reader);
    }
    
    /*
    public void testAttributes() throws Exception
    {
        Element e = new Element("root", "urn:test");
        e.setAttribute(new Attribute("att1", "value1"));
        e.setAttribute(new Attribute("att2",  "value2", Namespace.getNamespace("p", "urn:test2")));
        Document doc = new Document(e);
        
        W3CDOMStreamReader reader = new W3CDOMStreamReader(dom.output(doc).getDocumentElement());
        
        testAttributes(reader);
    }
    
    public void testElementChild() throws Exception
    {
        Element e = new Element("root", "urn:test");
        Element child = new Element("child", "a", "urn:test2");
        e.addContent(child);
        Document doc = new Document(e);
        
        W3CDOMStreamReader reader = new W3CDOMStreamReader(dom.output(doc).getDocumentElement());
        testElementChild(reader);
    }*/
}
