package org.codehaus.xfire.xmpp;

import java.io.StringReader;

import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.util.STAXUtils;
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

    public ChannelPacketListener(XMPPChannel channel)
    {
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
        XMLStreamReader reader = STAXUtils.createXMLStreamReader(new StringReader(soapPacket.getChildElementXML()));
        InMessage message = new InMessage(reader, to);
        
        MessageContext context = new MessageContext();
        context.setService(channel.getService());
        context.setProperty(PACKET, packet);
        
        channel.receive(context, message);
    }
}
