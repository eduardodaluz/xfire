package org.codehaus.xfire.xmpp;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.binding.DefaultObjectService;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.xfire.xmpp.client.EchoHandler;
import org.codehaus.xfire.xmpp.client.XMPPClient;
import org.codehaus.yom.Document;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.ToContainsFilter;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class TransportTest
    extends AbstractXFireAegisTest
{
    private DefaultObjectService echo;
    
    XMPPConnection conn;
    
    String username = "xfireTestServer";
    String password = "password1";
    String server = "bloodyxml.com";
    String id = username + "@" + server;
    
    public void setUp() 
        throws Exception
    {
        super.setUp();
        try
        {
            echo = (DefaultObjectService) getServiceFactory().create(Echo.class);

            getServiceRegistry().register( echo );

            //XMPPConnection.DEBUG_ENABLED = true;
            conn = new XMPPConnection(server);
            conn.login(username, password, "Echo");
            
            XFirePacketListener listener = new XFirePacketListener(getXFire(), conn);
            conn.addPacketListener(listener, new ToContainsFilter("xfireTestServer"));
        }
        catch (XMPPException e)
        {
            e.printStackTrace();
        }
    }

    protected void tearDown()
        throws Exception
    {
        conn.close();
        
        super.tearDown();
    }
        
    public void testTransport() throws Exception
    {
        try
        {
            XMPPClient client = new XMPPClient("bloodyxml.com", 
                                               "xfireTestClient",
                                               "password2",
                                               "Echo",
                                               id + "/Echo",
                                               new EchoHandler());
    
            client.invoke();
            client.close();
        }
        catch (XMPPException e)
        {
            e.printStackTrace();
        }
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
                    "/swsdl:address[@location='xfiretestserver@bloodyxml.com/Echo']", wsdl);
    }
}
