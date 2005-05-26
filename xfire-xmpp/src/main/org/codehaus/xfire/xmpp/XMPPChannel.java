package org.codehaus.xfire.xmpp;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.transport.AbstractChannel;
import org.codehaus.xfire.util.STAXUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.ToContainsFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.XMPPError;

public class XMPPChannel
    extends AbstractChannel
{
    private XMPPConnection conn;
    
    public XMPPChannel(String uri, XMPPTransport transport)
    {
        setUri(uri);
        setTransport(transport);
    }

    public void open()
    {
        if (conn != null)
            return;
        
        XMPPTransport transport = (XMPPTransport) getTransport();
        
        try
        {
            conn = new XMPPConnection(transport.getServer());
            conn.login(transport.getUsername(), transport.getPassword(), getUri());
            
            conn.addPacketListener(new ChannelPacketListener(this),
                                   new ToContainsFilter(transport.getUsername()));
        }
        catch (XMPPException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public void send(MessageContext context, OutMessage message)
        throws XFireFault
    {
        XMPPTransport transport = (XMPPTransport) getTransport();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, message.getEncoding());
        
        MessageSerializer ser = message.getSerializer();
        ser.writeMessage(message, writer, context);
        
        try
        {
            writer.flush();
            writer.close();
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        SoapEnvelopePacket response = new SoapEnvelopePacket(readDocument(out.toString()));
        response.setFrom(conn.getUser());
        response.setTo(message.getUri());
        response.setType(IQ.Type.RESULT);

        String packetId = (String) context.getProperty(ChannelPacketListener.PACKET_ID);
        if (packetId != null)
            response.setPacketID("");

        XMPPError error = (XMPPError) context.getProperty(XMPPFaultHandler.XMPP_ERROR);
        if (error != null)
            response.setError(error);

        conn.sendPacket(response);
    }

    public void close()
    {
        conn.close();
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
