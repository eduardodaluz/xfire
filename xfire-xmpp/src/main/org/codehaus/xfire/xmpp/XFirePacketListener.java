package org.codehaus.xfire.xmpp;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.TransportManager;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
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
        if (log.isDebugEnabled())
            log.debug("Got packet: " + packet.getClass().getName());

        if(!(packet instanceof SoapEnvelopePacket))
            // Just discard the packet?
            return;
        
        SoapEnvelopePacket soapPacket = (SoapEnvelopePacket) packet;
        
        String to = packet.getTo();
        String serviceName = to.substring(to.indexOf('/')+1);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageContext context = 
            new MessageContext(serviceName, 
                               null, 
                               out, 
                               null, 
                               to);
        context.setTransport(transport);

        xfire.invoke(soapPacket.getDocumentInputStream(), context);
        
        SoapEnvelopePacket response = new SoapEnvelopePacket(readDocument(out.toString()));
        response.setFrom(to);
        response.setTo(packet.getFrom());
        response.setType(IQ.Type.RESULT);
        response.setPacketID(soapPacket.getPacketID());

        conn.sendPacket(response);
    }
    
    protected Document readDocument(String text)
    {
        try
        {
            SAXReader reader = new SAXReader();
            return reader.read(new StringReader(text));
        }
        catch (DocumentException e)
        {
            throw new XFireRuntimeException("Couldn't read response document: " + text, e);
        }
    }
}
