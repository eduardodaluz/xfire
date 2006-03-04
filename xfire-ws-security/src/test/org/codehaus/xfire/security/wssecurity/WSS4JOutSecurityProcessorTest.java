package org.codehaus.xfire.security.wssecurity;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import junit.framework.TestCase;

import org.codehaus.xfire.security.SecurityActions;
import org.codehaus.xfire.security.impl.PropertiesLoader;
import org.codehaus.xfire.util.DOMUtils;
import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSS4JOutSecurityProcessorTest
    extends TestCase
{
    private Map namespaces;

    public WSS4JOutSecurityProcessorTest()
    {
        namespaces = new HashMap();
        namespaces.put("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        namespaces.put("ds", "http://www.w3.org/2000/09/xmldsig#");
    }

    public void testProcessor()
        throws Exception
    {
        InputStream inStream = getClass().getClassLoader()
                .getResourceAsStream("META-INF/xfire/request.xml");
        Document doc = DOMUtils.readXml(inStream);
        WSS4JOutSecurityProcessor processor = new WSS4JOutSecurityProcessor();
        OutSecurityDefaultBuilder builder = new OutSecurityDefaultBuilder ();
        builder.setConfiguration(new PropertiesLoader().loadConfigFile("META-INF/xfire/outsecurity_sign.properties"));
        builder.build(processor);
        Document encrypted = processor.process(doc).getDocument();

        assertValid(encrypted, "//wsse:Security", namespaces);
        assertValid(encrypted, "//wsse:Security/ds:Signature", namespaces);
    }

    public void testProcessorUserToken()
        throws Exception
    {
        InputStream inStream = getClass().getClassLoader().getResourceAsStream("META-INF/xfire/request.xml");
        Document doc = DOMUtils.readXml(inStream);
        WSS4JOutSecurityProcessor processor = new WSS4JOutSecurityProcessor();
        processor.setActions(new String[] { SecurityActions.AC_USERTOKEN });
        processor.setUsername("userName");
        processor.setUserPassword("userNamePass");
        
        Document encrypted = processor.process(doc).getDocument();

        assertValid(encrypted, "//wsse:Security", namespaces);
        assertValid(encrypted, "//wsse:Security/wsse:UsernameToken", namespaces);
        assertValid(encrypted, "//wsse:Security/wsse:UsernameToken/wsse:Username[text()='userName']", namespaces);
    }
    
    public Object assertValid(Node node, String path, Map namespaces) throws Exception
    {
        Object o = selectNode(node, path, namespaces);
        if (o == null) 
        {
            DOMUtils.writeXml(node, System.out);
            fail("Couldn't find node for " + path + ".");
        }
        
        return o;
    }
    
    public List selectNodes(Node node, String path, Map namespaces) throws JaxenException 
    {
        DOMXPath xpath = new DOMXPath(path);
        for (Iterator itr = namespaces.entrySet().iterator(); itr.hasNext();)
        {
            Map.Entry entry = (Map.Entry) itr.next();
            xpath.addNamespace((String) entry.getKey(), (String) entry.getValue());
        }
        
        return xpath.selectNodes(node);
    }
    
    public Object selectNode(Node node, String path, Map namespaces) throws JaxenException 
    {
        List nodes = selectNodes(node, path, namespaces);
        if (nodes.size() == 0) return null;
        
        return nodes.get(0);
    }
}
