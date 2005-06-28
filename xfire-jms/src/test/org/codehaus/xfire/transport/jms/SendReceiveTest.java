package org.codehaus.xfire.transport.jms;

import javax.xml.stream.XMLStreamException;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.ChannelEndpoint;
import org.codehaus.xfire.util.YOMSerializer;
import org.codehaus.yom.Document;
import org.codehaus.yom.stax.StaxBuilder;

public class SendReceiveTest
    extends AbstractXFireJMSTest
{
    public void testSend()
        throws Exception
    {
        String peer1 = "Peer1";
        String peer2 = "Peer2";

        Channel channel1 = getTransport().createChannel(peer1);
        Channel channel2 = getTransport().createChannel(peer2);
        channel2.setEndpoint(new YOMEndpoint());

        // Document to send
        StaxBuilder builder = new StaxBuilder();
        Document doc = builder.build(getResourceAsStream("/org/codehaus/xfire/transport/jms/echo.xml"));

        MessageContext mc = new MessageContext();

        OutMessage msg = new OutMessage(peer2);
        msg.setSerializer(new YOMSerializer());
        msg.setBody(doc);

        channel1.send(mc, msg);
        channel1.send(mc, msg);
        
        Thread.sleep(1000);
    }
/*
    public void testWSDL()
        throws Exception
    {
        Document wsdl = getWSDLDocument("Echo");

        addNamespace("wsdl", WSDLWriter.WSDL11_NS);
        addNamespace("swsdl", WSDLWriter.WSDL11_SOAP_NS);

        assertValid("//wsdl:binding[@name='EchoXMPPBinding'][@type='tns:EchoPortType']", wsdl);
        assertValid("//wsdl:binding[@name='EchoXMPPBinding']/swsdl:binding[@transport='"
                + JMSTransport.NAME + "']", wsdl);

        assertValid("//wsdl:service/wsdl:port[@binding='tns:EchoXMPPBinding'][@name='EchoXMPPPort']",
                    wsdl);
        assertValid("//wsdl:service/wsdl:port[@binding='tns:EchoXMPPBinding'][@name='EchoXMPPPort']"
                            + "/swsdl:address[@location='xfireTestServer@bloodyxml.com/Echo']",
                    wsdl);
    }*/

    public class YOMEndpoint
        implements ChannelEndpoint
    {
        private int count = 0;

        public void onReceive(MessageContext context, InMessage msg)
        {
            count++;

            StaxBuilder builder = new StaxBuilder();
            try
            {
                Document doc = builder.build(msg.getXMLStreamReader());
                System.out.println("Received message.");
            }
            catch (XMLStreamException e)
            {
                e.printStackTrace();
            }
        }

        public int getCount()
        {
            return count;
        }
    }
}
