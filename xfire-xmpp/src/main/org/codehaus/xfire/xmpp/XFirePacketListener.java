package org.codehaus.xfire.xmpp;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.TransportManager;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XFirePacketListener
    implements PacketListener
{
    private static Log log = LogFactory.getLog(XFirePacketListener.class);
    
    private XFire xfire;
    private Transport transport;
    private XMPPConnection conn;
    
    public XFirePacketListener(XFire xfire, XMPPConnection conn)
    {
        this.xfire = xfire;
        this.conn = conn;
        
        // Register the XMPP transport.
        TransportManager manager = xfire.getTransportManager();
        transport = manager.getTransport(XMPPTransport.NAME);
        if (transport == null)
            manager.register(new XMPPTransport(conn));
    }

    /**
     * @param packet
     */
    public void processPacket(Packet packet)
    {
        System.err.println("Got packet: " + packet.getClass().getName());
        
        if (log.isDebugEnabled())
            log.debug("Got packet: " + packet.getClass().getName());

        System.err.println("Got packet: " + packet.getClass().getName());
        System.err.println(packet.getClass().getName());
        
        if(!(packet instanceof SoapBodyPacket))
            // Just discard the packet?
            return;
        
        SoapBodyPacket soapPacket = (SoapBodyPacket) packet;
        
        String to = packet.getTo();
        String serviceName = to.substring(to.indexOf('/'));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageContext context = 
            new MessageContext(serviceName, 
                               null, 
                               out, 
                               null, 
                               to);
        context.setTransport(transport);

        xfire.invoke(soapPacket.getDocumentInputStream(), context);
        
        SoapBodyPacket response = new SoapBodyPacket(out.toString());
        response.setFrom(to);
        response.setTo(packet.getFrom());
        response.setType(IQ.Type.RESULT);
        response.setPacketID(soapPacket.getPacketID());

        conn.sendPacket(response);
    }
}
