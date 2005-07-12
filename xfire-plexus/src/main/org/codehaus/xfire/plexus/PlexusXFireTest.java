package org.codehaus.xfire.plexus;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.test.XPathAssert;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.local.LocalTransport;
import org.codehaus.xfire.util.STAXUtils;
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
public class PlexusXFireTest
        extends PlexusTestCase
{
    /**
     * Namespaces for the XPath expressions.
     */
    private Map namespaces = new HashMap();

    protected void printNode(Node node)
            throws Exception
    {
        Serializer writer = new Serializer(System.out);
        writer.setOutputStream(System.out);

        if (node instanceof Document)
            writer.write((Document) node);
        else
        {
            writer.flush();
            writer.writeChild(node);
        }
    }

    /**
     * Invoke a service with the specified document.
     *
     * @param service  The name of the service.
     * @param document The request as an xml document in the classpath.
     */
    protected Document invokeService(String service, String document)
            throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageContext context = new MessageContext();
        context.setXFire(getXFire());
        context.setProperty(Channel.BACKCHANNEL_URI, out);

        if (service != null)
            context.setService(getServiceRegistry().getService(service));
        
        InputStream stream = getResourceAsStream(document); 
        InMessage msg = new InMessage(STAXUtils.createXMLStreamReader(stream, "UTF-8"));

        Transport t = getXFire().getTransportManager().getTransport(LocalTransport.NAME);
        Channel c = t.createChannel();

        c.receive(context, msg);
        
        String response = out.toString();
        if (response == null || response.length() == 0)
            return null;

        return readDocument(response);
    }

    protected Document readDocument(String text)
            throws XMLStreamException
    {
        try
        {
            StaxBuilder builder = new StaxBuilder();
            return builder.build(new StringReader(text));
        }
        catch (XMLStreamException e)
        {
            System.err.println("Could not read the document!");
            System.out.println(text);
            throw e;
        }
    }

    protected Document getWSDLDocument(String service)
            throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        getXFire().generateWSDL(service, out);

        return readDocument(out.toString());
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp()
            throws Exception
    {
        super.setUp();

        addNamespace("s", Soap11.getInstance().getNamespace());
        addNamespace("soap12", Soap12.getInstance().getNamespace());
    }

    /**
     * Assert that the following XPath query selects one or more nodes.
     *
     * @param xpath
     * @return
     */
    public List assertValid(String xpath, Node node)
            throws Exception
    {
        return XPathAssert.assertValid(xpath, node, namespaces);
    }

    /**
     * Assert that the following XPath query selects no nodes.
     *
     * @param xpath
     * @return
     */
    public List assertInvalid(String xpath, Node node)
            throws Exception
    {
        return XPathAssert.assertInvalid(xpath, node, namespaces);
    }

    /**
     * Asser that the text of the xpath node retrieved is equal to the value specified.
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
     *
     * @param ns  Namespace name.
     * @param uri The namespace uri.
     */
    public void addNamespace(String ns, String uri)
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

    protected XFire getXFire()
            throws Exception
    {
        return (XFire) lookup(XFire.ROLE);
    }

    protected ServiceRegistry getServiceRegistry()
            throws Exception
    {
        return getXFire().getServiceRegistry();
    }
}
