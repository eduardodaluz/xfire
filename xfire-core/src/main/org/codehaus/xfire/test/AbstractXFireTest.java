package org.codehaus.xfire.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import junit.framework.TestCase;

import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.yom.Document;
import org.codehaus.yom.Node;
import org.codehaus.yom.Serializer;
import org.codehaus.yom.stax.StaxBuilder;

/**
 * Contains helpful methods to test SOAP services.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public abstract class AbstractXFireTest
    extends TestCase
{
    private XFire xfire;

    /**
     * Namespaces for the XPath expressions.
     */
    private Map namespaces = new HashMap();

    protected void printNode( Node node )
        throws Exception
    {
        Serializer writer = new Serializer( System.out );
        writer.setOutputStream( System.out );
        
        if (node instanceof Document)
            writer.write((Document) node);
        else
        {
            writer.flush();
            writer.writeChild( node );
        }
    }

    /**
     * Invoke a service with the specified document.
     *
     * @param service  The name of the service.
     * @param document The request as an xml document in the classpath.
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

        return readDocument( out.toString() );
    }

    protected Document readDocument( String text )
        throws XMLStreamException
    {
        try
        {
            StaxBuilder builder = new StaxBuilder();
            return builder.build( new StringReader( text ) );
        }
        catch( XMLStreamException e )
        {
            System.err.println( "Could not read the document!" );
            System.out.println( text );
            throw e;
        }
    }

    protected Document getWSDLDocument( String service )
        throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        getXFire().generateWSDL(service, out);

        return readDocument(out.toString());
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        xfire = new DefaultXFire();

        addNamespace( "s", Soap11.getInstance().getNamespace() );
        addNamespace( "soap12", Soap12.getInstance().getNamespace() );
    }

    /**
     * Assert that the following XPath query selects one or more nodes.
     *
     * @param xpath
     */
    public void assertValid( String xpath, Node node )
        throws Exception
    {
        XPathAssert.assertValid(xpath, node, namespaces);
    }

    /**
     * Assert that the following XPath query selects no nodes.
     *
     * @param xpath
     */
    public void assertInvalid( String xpath, Node node )
        throws Exception
    {
        XPathAssert.assertInvalid(xpath, node, namespaces);
    }

    /**
     * Asser that the text of the xpath node retrieved is equal to the value specified.
     *
     * @param xpath
     * @param value
     * @param node
     */
    public void assertXPathEquals( String xpath, String value, Node node )
        throws Exception
    {
        XPathAssert.assertXPathEquals(xpath, value, node, namespaces);
    }

    public void assertNoFault( Node node )
        throws Exception
    {
        XPathAssert.assertNoFault(node);
    }

    /**
     * Add a namespace that will be used for XPath expressions.
     *
     * @param ns  Namespace name.
     * @param uri The namespace uri.
     */
    public void addNamespace( String ns, String uri )
    {
        namespaces.put( ns, uri );
    }

    /**
     * Get the WSDL for a service.
     *
     * @param service The name of the service.
     */
    protected WSDLWriter getWSDL( String service )
        throws Exception
    {
        ServiceRegistry reg = getServiceRegistry();
        Service hello = reg.getService( service );

        return hello.getWSDLWriter();
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
        return getClass().getResourceAsStream( resource );
    }

    protected Reader getResourceAsReader( String resource )
    {
        return new InputStreamReader( getResourceAsStream( resource ) );
    }

    public File getTestFile( String relativePath )
    {
        String basedir = System.getProperty( "basedir" );
        if( basedir == null )
            basedir = "./";

        return new File( new File( basedir ), relativePath );
    }
}