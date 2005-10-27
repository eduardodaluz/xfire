package org.codehaus.xfire.transport.jms;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.util.jdom.JDOMEndpoint;
import org.codehaus.xfire.util.jdom.JDOMSerializer;
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.jdom.Document;

public class SendReceiveTest
    extends AbstractXFireJMSTest
{
    public void testSend()
        throws Exception
    {
        String peer1 = "jms://Peer1";
        String peer2 = "jms://Peer2";

        Channel channel1 = getTransport().createChannel(peer1);
        Channel channel2 = getTransport().createChannel(peer2);
        channel2.setEndpoint(new JDOMEndpoint());

        // Document to send
        StaxBuilder builder = new StaxBuilder();
        Document doc = builder.build(getResourceAsStream("/org/codehaus/xfire/transport/jms/echo.xml"));

        MessageContext mc = new MessageContext();

        OutMessage msg = new OutMessage(peer2);
        msg.setSerializer(new JDOMSerializer());
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
}
