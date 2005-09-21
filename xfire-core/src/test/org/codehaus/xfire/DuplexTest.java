package org.codehaus.xfire;


import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.local.LocalTransport;
import org.codehaus.xfire.util.YOMEndpoint;
import org.codehaus.xfire.util.YOMSerializer;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;

public class DuplexTest
    extends AbstractXFireTest
{
    public void testPeer1() throws Exception
    {
        LocalTransport transport = new LocalTransport();
        Channel channel1 = transport.createChannel("urn:xfire:local:Peer1");
        channel1.open();
        
        Channel channel2 = transport.createChannel("urn:xfire:local:Peer2");
        YOMEndpoint endpoint = new YOMEndpoint();
        channel2.setEndpoint(endpoint);
        
        // Document to send
        Element root = new Element("root");
        root.appendChild("hello");
        Document doc = new Document(root);
        
        MessageContext context = new MessageContext();
        
        OutMessage msg = new OutMessage("urn:xfire:local:Peer2");
        msg.setSerializer(new YOMSerializer());
        msg.setBody(doc);
        
        channel1.send(context, msg);
        channel1.send(context, msg);
        
        for (int i = 0; i < 100; i++)
        {
            Thread.sleep(50);
            if (endpoint.getCount() == 2) break;
        }
        
        channel1.close();
        channel2.close();
        
        assertEquals(2, endpoint.getCount());
    }
}
