package org.codehaus.xfire.xmpp.client;

import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.client.ClientHandler;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.xmpp.SoapEnvelopePacket;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XMPPClient
{
    private XMPPConnection conn;
    private String service;
    private ClientHandler clientHandler;
    private String from;
    
    public XMPPClient(String host, 
                      String username, 
                      String password,
                      String resource,
                      String service,
                      ClientHandler clientHandler) 
        throws XMPPException
    {
        conn = new XMPPConnection(host);
        conn.login(username, password, resource);
        from = username + "@" + host + "/" + resource;
        
        this.service = service;
        this.clientHandler = clientHandler;
    }
    
    public void invoke() 
        throws XFireFault
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer;
        try
        {
            writer = factory.createXMLStreamWriter(out);
            
            clientHandler.writeRequest(writer);
            writer.close();
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Couldn't write request.", XFireFault.SENDER);
        }
        
        SoapEnvelopePacket packet = new SoapEnvelopePacket(out.toString());
        packet.setType(IQ.Type.GET);
        packet.setFrom(from);
        packet.setTo(service);
        
        // Create a packet collector to listen for a response.
        PacketCollector collector = 
            conn.createPacketCollector(new PacketIDFilter(packet.getPacketID()));

        conn.sendPacket(packet);

        // Wait up to 5 seconds for a result.
        IQ result = (IQ) collector.nextResult(5000);
        if (result != null 
            && 
            result.getType() == IQ.Type.RESULT
            &&
            result instanceof SoapEnvelopePacket) 
        {
            SoapEnvelopePacket env = (SoapEnvelopePacket) result;

            try
            {
                XMLInputFactory iFactory = XMLInputFactory.newInstance();
                XMLStreamReader reader = 
                    iFactory.createXMLStreamReader(env.getDocumentInputStream());
                
                clientHandler.handleResponse( reader );
            }
            catch (XMLStreamException e)
            {
                throw new XFireRuntimeException("Couldn't parse stream.", e);
            }
        }
    }
    
    public void close()
    {
        conn.close();
    }
}
