package org.codehaus.xfire;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.wsdl.WSDL;
import org.dom4j.Document;
import org.dom4j.DocumentException;
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
public class AbstractXFireTest
    extends TestCase
{
    private XFire xfire;
    
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
        
        return readDocument(out.toString());
    }

    protected Document readDocument(String text)
    	throws DocumentException
    {
        try
        {
            SAXReader reader = new SAXReader();
            return reader.read( new StringReader(text) );
        }
        catch( DocumentException e )
        {
            System.err.println("Could not read the document!");
            System.out.println(text);
            throw e;
        }
    }

    protected Document getWSDLDocument( String service ) 
        throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        getXFire().generateWSDL( service, out );
        
        try
        {
            SAXReader reader = new SAXReader();
            return reader.read( new StringReader(out.toString()) );
        }
        catch( DocumentException e )
        {
            System.err.println("Could not read the document!");
            System.out.println(out.toString());
            throw e;
        }
    }
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        
        BasicConfigurator.configure();
        
        xfire = new DefaultXFire();
        
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
    
    protected XFire getXFire()
    {
        return xfire;
    }
    
    protected ServiceRegistry getServiceRegistry()
    {
        return getXFire().getServiceRegistry();
    }
    
    protected InputStream getResourceAsStream( String resource )
    {
        return getClass().getResourceAsStream(resource);
    }

    protected Reader getResourceAsReader( String resource )
    {
        return new InputStreamReader( getResourceAsStream(resource) );
    }
}