package org.codehaus.xfire.plexus;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.StringInputStream;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.SOAPConstants;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.wsdl.WSDL;
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
        
        addNamespace("s", SOAPConstants.SOAP11_ENVELOPE_NS );
        addNamespace("soap12", SOAPConstants.SOAP12_ENVELOPE_NS);
    }
    
    /**
     * Assert that the following XPath query selects one or more nodes.
     * 
     * @param xpath
     * @throws Exception
     */
    public void assertValid( String xpath, Node node )
        throws Exception
    {
        List nodes = createXPath( xpath ).selectNodes( node );
        
        if ( nodes.size() == 0 )
        {
            throw new Exception( "Failed to select any nodes for expression:.\n" +
                                 xpath + "\n" +
                                 node.asXML() );
        }
    }
    
    /**
     * Assert that the following XPath query selects no nodes.
     * 
     * @param xpath
     * @throws Exception
     */
    public void assertInvalid( String xpath, Node node )
        throws Exception
    {
        List nodes = createXPath( xpath ).selectNodes( node );
        
        if ( nodes.size() > 0 )
        {
            throw new Exception( "Found multiple nodes for expression:\n" +
                                 xpath + "\n" +
                                 node.asXML() );
        }
    }

    /**
     * Asser that the text of the xpath node retrieved is equal to the
     * value specified.
     * 
     * @param xpath
     * @param value
     * @param node
     * @throws Exception
     */
    public void assertXPathEquals( String xpath, String value, Node node )
        throws Exception
    {
        String value2 = createXPath( xpath ).selectSingleNode( node ).getText().trim();
        
        assertEquals( value, value2 );
    }
    
    public void assertNoFault( Node node )
        throws Exception
    {
        assertInvalid("/s:Envelope/s:Body/s:Fault", node);
    }
    
    /**
     * Create the specified XPath expression with the namespaces added
     * via addNamespace().
     */
    protected XPath createXPath( String xpathString )
    {
        XPath xpath = DocumentHelper.createXPath( xpathString );
        xpath.setNamespaceURIs(namespaces);
        
        return xpath;
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
    protected WSDL getWSDL(String service) 
        throws Exception
    {
        ServiceRegistry reg = getServiceRegistry();
        Service hello = reg.getService(service);
        
        return hello.getWSDL();
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
