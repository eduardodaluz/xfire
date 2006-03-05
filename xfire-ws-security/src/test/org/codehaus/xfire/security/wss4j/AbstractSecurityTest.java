package org.codehaus.xfire.security.wss4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.util.DOMUtils;
import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class AbstractSecurityTest extends TestCase 
{

    private Map namespaces;

    public AbstractSecurityTest() 
    {
        super();
        
        namespaces = new HashMap();
        namespaces.put("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        namespaces.put("ds", "http://www.w3.org/2000/09/xmldsig#");
        namespaces.put("s", Soap11.getInstance().getNamespace());
        namespaces.put("xenc", "http://www.w3.org/2001/04/xmlenc#");
        namespaces.put("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
    }

    protected void printNode(Node document) throws TransformerException
    {
        DOMUtils.writeXml(document, System.out);
    }
    
    protected void addNamespace(String prefix, String uri)
    {
        namespaces.put(prefix, uri);
    }
    
    protected Object assertValid(Node node, String path) throws Exception
    {
        Object o = selectNode(node, path);
        if (o == null) 
        {
            DOMUtils.writeXml(node, System.out);
            fail("Couldn't find node for " + path + ".");
        }
        
        return o;
    }
    
    protected List selectNodes(Node node, String path) throws JaxenException 
    {
        DOMXPath xpath = new DOMXPath(path);
        for (Iterator itr = namespaces.entrySet().iterator(); itr.hasNext();)
        {
            Map.Entry entry = (Map.Entry) itr.next();
            xpath.addNamespace((String) entry.getKey(), (String) entry.getValue());
        }
        
        return xpath.selectNodes(node);
    }
    
    protected Object selectNode(Node node, String path) throws JaxenException 
    {
        List nodes = selectNodes(node, path);
        if (nodes.size() == 0) return null;
        
        return nodes.get(0);
    }

    protected Document readDocument(String name) 
        throws SAXException, IOException, ParserConfigurationException 
    {
        InputStream inStream = getClass().getResourceAsStream(name);
        Document doc = DOMUtils.readXml(inStream);
        return doc;
    }
}
