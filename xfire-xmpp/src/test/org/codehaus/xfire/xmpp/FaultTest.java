package org.codehaus.xfire.xmpp;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.binding.DefaultObjectService;
import org.codehaus.xfire.xmpp.client.EchoHandler;
import org.codehaus.xfire.xmpp.client.XMPPClient;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.ToContainsFilter;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class FaultTest
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
            echo = (DefaultObjectService) getServiceFactory().create(BadEcho.class);

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
                                               id + "/BadEcho",
                                               new EchoHandler());
    
            client.invoke();

            client.close();
        }
        catch (XMPPException e)
        {
            e.printStackTrace();
        }
    }
}
