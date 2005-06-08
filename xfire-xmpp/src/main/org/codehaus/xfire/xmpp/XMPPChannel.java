package org.codehaus.xfire.xmpp;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.transport.AbstractSoapChannel;
import org.codehaus.xfire.transport.Channel;
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
    extends AbstractSoapChannel
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
            e.printStackTrace();
        }
    }

    public void send(MessageContext context, OutMessage message)
        throws XFireFault
    {
        XMPPTransport transport = (XMPPTransport) getTransport();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            final XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, message.getEncoding());
            sendSoapMessage(message, writer, context);
            
            writer.flush();
            writer.close();
            
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException("Couldn't write stream.", e);
        }

        SoapEnvelopePacket response = new SoapEnvelopePacket(readDocument(out.toString()));
        response.setFrom(conn.getUser());
        
        if (message.getUri().equals(Channel.BACKCHANNEL_URI))
        {
            SoapEnvelopePacket req = 
                    (SoapEnvelopePacket) context.getProperty(ChannelPacketListener.PACKET);
            response.setTo(req.getFrom());
            response.setType(IQ.Type.RESULT);
            response.setPacketID(req.getPacketID());
        }
        else
        {
            response.setTo(message.getUri());
        }
        
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
