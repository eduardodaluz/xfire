package org.codehaus.xfire.xmpp;

import org.codehaus.xfire.java.ServiceHelper;
import org.codehaus.xfire.java.test.AbstractXFireJavaTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.xmpp.client.EchoHandler;
import org.codehaus.xfire.xmpp.client.XMPPClient;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.ToContainsFilter;
import org.jivesoftware.smack.packet.Presence;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class TransportTest
    extends AbstractXFireJavaTest
{
    private Service echo;
    
    XMPPConnection conn;
    
    String username = "xfireTestServer";
    String password = "password1";
    String server = "jabber.org";
    String id = username + "@" + server;
    
    public void setUp() 
        throws Exception
    {
        super.setUp();
        try
        {
            echo = ServiceHelper.createService(getXFire(), getRegistry(), Echo.class);
    
            XMPPConnection.DEBUG_ENABLED = true;
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
            XMPPClient client = new XMPPClient("jabber.org", 
                                               "xfireTestClient", 
                                               "password2",
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
    
    /*public void testWSDL()
        throws Exception
    {
        Document wsdl = getWSDLDocument("Echo");
        printNode(wsdl);
    }*/
}
