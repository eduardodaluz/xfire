package org.codehaus.xfire.plexus.transport.xmpp;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.xmpp.XFirePacketListener;
import org.jivesoftware.smack.XMPPConnection;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DefaultXMPPTransportService
    implements Initializable
{
    private String username;
    private String password;
    private String server;
    private String resource;
    
    private XMPPConnection conn;
    private XFirePacketListener transport;
    private XFire xfire;
    
    /**
     * @throws Exception
     */
    public void initialize()
        throws Exception
    {
        conn = new XMPPConnection(server);
        conn.login(username, password, resource);
        
        transport = new XFirePacketListener(xfire, conn);
    }
    
    public XMPPConnection getXMPPConnection()
    {
        return conn;
    }
}
