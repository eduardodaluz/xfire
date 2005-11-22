package org.codehaus.xfire.xmpp;

import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapSerializer;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.util.jdom.JDOMEndpoint;
import org.codehaus.xfire.util.jdom.JDOMSerializer;
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.jdom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class TransportTest
        extends AbstractXFireAegisTest
{
    private Service echo;

    private Transport transport1;
    private Transport transport2;

    String username = "xfireTestServer";
    String password = "password1";
    String server = "bloodyxml.com";
    String id = username + "@" + server;

    public void setUp()
            throws Exception
    {
        super.setUp();

        transport2 = new XMPPTransport(getXFire(), server, username, password);
        getTransportManager().register(transport2);
        
        transport1 = new XMPPTransport(getXFire(), server, "xfireTestClient", "password2");

        echo = getServiceFactory().create(Echo.class);

        getServiceRegistry().register(echo);
        // XMPPConnection.DEBUG_ENABLED = true;
    }

    protected void tearDown()
        throws Exception
    {
        transport1.dispose();
        transport2.dispose();
        
        super.tearDown();
    }

    public void testTransport()
            throws Exception
    {
        String peer1 = "Peer1";
        String peer2 = "Peer2";
        
        Channel channel1 = transport1.createChannel(peer1);
        Channel channel2 = transport2.createChannel(peer2);
        JDOMEndpoint endpoint = new JDOMEndpoint();
        channel2.setEndpoint(endpoint);
        
        // Document to send
        StaxBuilder builder = new StaxBuilder();
        Document doc = builder.build(getResourceAsStream("/org/codehaus/xfire/xmpp/echo.xml"));

        MessageContext context = new MessageContext();

        OutMessage msg = new OutMessage(id + "/" + peer2);
        msg.setSerializer(new SoapSerializer(new JDOMSerializer()));
        msg.setBody(doc);

        channel1.send(context, msg);
        
        for (int i = 0; i < 100; i++)
        {
            Thread.sleep(50);
            if (endpoint.getCount() == 1) break;
        }

        assertEquals(1, endpoint.getCount());
    }
    
    public void testService()
            throws Exception
    {
        String peer1 = "Peer1";
        
        Channel channel1 = transport1.createChannel(peer1);

        JDOMEndpoint peer = new JDOMEndpoint();
        channel1.setEndpoint(peer);
        
        Channel channel2 = transport2.createChannel("Echo");

        Binding binding = echo.getBinding(transport2);
        assertNotNull(binding);
        
        // Document to send
        StaxBuilder builder = new StaxBuilder();
        Document doc = builder.build(getResourceAsStream("/org/codehaus/xfire/xmpp/echo.xml"));
        
        MessageContext context = new MessageContext();

        OutMessage msg = new OutMessage(id + "/Echo");
        msg.setSerializer(new SoapSerializer(new JDOMSerializer()));
        msg.setBody(doc);

        channel1.send(context, msg);

        for (int i = 0; i < 100; i++)
        {
            Thread.sleep(50);
            if (peer.getCount() == 1) break;
        }
        
        assertEquals(1, peer.getCount());
    }

    public void testWSDL()
            throws Exception
    {
        Document wsdl = getWSDLDocument("Echo");

        addNamespace("wsdl", WSDLWriter.WSDL11_NS);
        addNamespace("swsdl", WSDLWriter.WSDL11_SOAP_NS);

        assertValid("//wsdl:binding[@name='EchoXMPPBinding'][@type='tns:EchoPortType']", wsdl);
        assertValid("//wsdl:binding[@name='EchoXMPPBinding']/swsdl:binding[@transport='" +
                    XMPPTransport.XMPP_TRANSPORT_NS + "']", wsdl);

        assertValid("//wsdl:service/wsdl:port[@binding='tns:EchoXMPPBinding'][@name='EchoXMPPPort']", wsdl);
        assertValid("//wsdl:service/wsdl:port[@binding='tns:EchoXMPPBinding'][@name='EchoXMPPPort']" +
                    "/swsdl:address[@location='xfireTestServer@bloodyxml.com/Echo']", wsdl);
    }
}
