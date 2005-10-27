package org.codehaus.xfire.xmpp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.util.stax.JDOMStreamReader;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class ChannelPacketListener
    implements PacketListener
{
    private static Log log = LogFactory.getLog(ChannelPacketListener.class);
    
    private XMPPChannel channel;

    public static final String PACKET = "xmpp.packet";

    private XFire xfire;
    
    public ChannelPacketListener(XFire xfire, XMPPChannel channel)
    {
        this.xfire = xfire;
        this.channel = channel;
    }

    /**
     * @param packet
     */
    public void processPacket(Packet packet)
    {
        if (log.isDebugEnabled())
            log.debug("Got packet: " + packet.getClass().getName());

        if(!(packet instanceof SoapEnvelopePacket))
            // Just discard the packet?
            return;
        
        SoapEnvelopePacket soapPacket = (SoapEnvelopePacket) packet;

        String to = packet.getTo();
        String serviceName = to.substring(to.indexOf('/')+1);
        Service service = xfire.getServiceRegistry().getService(serviceName);
        
        InMessage message = new InMessage(new JDOMStreamReader(soapPacket.getDocument().getRootElement()), to);

        MessageContext context = new MessageContext();
        context.setProperty(PACKET, packet);
        context.setXFire(xfire);
        context.setService(service);
        
        channel.receive(context, message);
    }
}
