package org.codehaus.xfire.plexus;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.StringInputStream;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.test.XPathAssert;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * Contains helpful methods to test SOAP services.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PlexusXFireTest
    extends PlexusTestCase
{
    /** Namespaces for the XPath expressions. */
    private Map namespaces = new HashMap();
    
    protected void printNode( Node node ) 
        throws Exception
    {
        XMLWriter writer = new XMLWriter( OutputFormat.createPrettyPrint() );
        writer.setOutputStream( System.out );
        writer.write( node );
    }
    
    /**
     * Invoke a service with the specified document.
     * 
     * @param service The name of the service.
     * @param document The request as an xml document in the classpath.
     * @return
     * @throws Exception
     */
    protected Document invokeService( String service, String document ) 
        throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageContext context = 
            new MessageContext( service,
                                null,
                                out,
                                null,
                                null );
        
        getXFire().invoke( getResourceAsStream( document ), context );
        
        SAXReader reader = new SAXReader();
        return reader.read( new StringInputStream(out.toString()) );
    }

    protected Document getWSDLDocument( String service ) 
        throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        getXFire().generateWSDL( service, out );
        
        SAXReader reader = new SAXReader();
        return reader.read( new StringInputStream(out.toString()) );
    }
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        
        addNamespace("s", Soap11.getInstance().getNamespace());
        addNamespace("soap12", Soap12.getInstance().getNamespace());
    }
    
    /**
     * Assert that the following XPath query selects one or more nodes.
     * 
     * @param xpath
     */
    public void assertValid(String xpath, Node node)
        throws Exception
    {
        XPathAssert.assertValid(xpath, node, namespaces);
    }

    /**
     * Assert that the following XPath query selects no nodes.
     * 
     * @param xpath
     */
    public void assertInvalid(String xpath, Node node)
        throws Exception
    {
        XPathAssert.assertInvalid(xpath, node, namespaces);
    }

    /**
     * Asser that the text of the xpath node retrieved is equal to the value
     * specified.
     * 
     * @param xpath
     * @param value
     * @param node
     */
    public void assertXPathEquals(String xpath, String value, Node node)
        throws Exception
    {
        XPathAssert.assertXPathEquals(xpath, value, node, namespaces);
    }

    public void assertNoFault(Node node)
        throws Exception
    {
        XPathAssert.assertNoFault(node);
    }
    
    /**
     * Add a namespace that will be used for XPath expressions.
     * @param ns Namespace name.
     * @param uri The namespace uri.
     */
    public void addNamespace( String ns, String uri )
    {
        namespaces.put(ns, uri);
    }

    /**
     * Get the WSDL for a service.
     * 
     * @param string The name of the service.
     * @return
     * @throws Exception
     */
    protected WSDLWriter getWSDL(String service) 
        throws Exception
    {
        ServiceRegistry reg = getServiceRegistry();
        Service hello = reg.getService(service);
        
        return hello.getWSDLWriter();
    }
    
    protected XFire getXFire() throws Exception
    {
        return (XFire) lookup( XFire.ROLE );
    }
    
    protected ServiceRegistry getServiceRegistry() throws Exception
    {
        return getXFire().getServiceRegistry();
    }
}
