package org.codehaus.xfire.xmpp;

import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.transport.Channel;
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
    
    private Channel channel;

    public static final String PACKET_ID = "xmpp.packetId";

    public ChannelPacketListener(Channel channel)
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
        String serviceName = to.substring(to.indexOf('/')+1);

        XMLStreamReader reader = STAXUtils.createXMLStreamReader(soapPacket.getDocumentInputStream(), "UTF-8");
        InMessage message = new InMessage(reader, to);
        
        MessageContext context = new MessageContext();
        context.setProperty(PACKET_ID, packet.getPacketID());
        
        channel.receive(context, message);
    }
}
