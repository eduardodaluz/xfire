package org.codehaus.xfire.transport.jms;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.transport.AbstractChannel;
import org.codehaus.xfire.util.STAXUtils;

public class JMSChannel
    extends AbstractChannel
    implements MessageListener
{
    public static final String REPLY_TO = "jms.replyTo";

    public static final String JMS_URI = "urn:codehaus:xfire:jms";

    private static final Log log = LogFactory.getLog(JMSChannel.class);

    private Session session;

    private Connection connection;

    private Destination destination;

    private MessageConsumer consumer;

    private boolean isTopic = false;

    private String selector = "";

    String MyID = java.util.UUID.randomUUID().toString();

    public JMSChannel(String uri, JMSTransport transport)
    {
        setUri(uri);
        setTransport(transport);
        getDestinationName(uri);
    }

    public void open()
        throws JMSException
    {
        if (session != null)
            return;

        JMSTransport transport = (JMSTransport) getTransport();
        connection = transport.getConnectionFactory().createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        String destinationName = getDestinationName(getUri());
        if (isTopic)
        {
            System.out.println("CREATING TOPIC");
            destination = session.createTopic(destinationName);
        }
        else
        {
            destination = session.createQueue(destinationName);
        }
        
        // Queues may not have a selector.
        if (selector.equals(""))
        {
            consumer = session.createConsumer(destination);
        }
        else
        {
            consumer = session.createConsumer(destination, "JMSType='" + selector + "' and Source<>'" + MyID
                    + "' and (Destination='" + MyID + "' or Destination='Service')");
        }
        
        consumer.setMessageListener(this);
    }

    // Modified to extract urls of the type:
    // jms://JMSServer/DestinationName?topic=Echo
    private String getDestinationName(String uri)
    {
        int i = uri.indexOf("://");
        if (i == -1)
            throw new XFireRuntimeException("Invalid JMS URI: " + uri);

        int posQMark = uri.indexOf("?", i + 4);
        String destName = "";
        if (posQMark > 0)
        {
            destName = uri.substring(i + 3, posQMark);
            // ok, so there is a question mark. This should be followed by a
            // name=value pair
            // Find the = sign, if not found, assume we're dealing with a queue
            // selector
            int posEqual = uri.indexOf("=", posQMark + 1);
            if (posEqual > 0)
            {
                if (uri.substring(posQMark + 1, posEqual).trim().equalsIgnoreCase("topic"))
                {
                    isTopic = true;
                }
                selector = uri.substring(posEqual + 1);
            }
            else
            {
                selector = uri.substring(posQMark + 1);
            }
            if (selector.equals("") || selector.equals("."))
            {
                selector = "";
            }
        }
        else
        {
            destName = uri.substring(i + 3);
            // The case for WebLogic module!queuename should set the selector as
            // well
            int posEMark = destName.indexOf("!");
            if (posEMark > 0)
            {
                selector = destName.substring(posEMark + 1);
            }
        }

        return destName;
    }

    public void send(MessageContext context, OutMessage message)
        throws XFireFault
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, message.getEncoding(), context);

        message.getSerializer().writeMessage(message, writer, context);

        try
        {
            writer.flush();
            writer.close();
            out.close();
        }
        catch (Exception e)
        {
            log.error("Error serializing message", e);
        }

        try
        {
            Destination dest = ((Destination) context.getProperty(REPLY_TO));
            String responseUri = message.getUri();
            TextMessage jmsMessage = session.createTextMessage();
            jmsMessage.setText(out.toString());
            jmsMessage.setJMSCorrelationID(context.getId());

            if (!isTopic)
            {
                TemporaryQueue reply = session.createTemporaryQueue();
                session.createConsumer(reply).setMessageListener(this);
                jmsMessage.setJMSReplyTo(reply);

                // Queues are point to point
                if (dest == null)
                {
                    String destName = getDestinationName(responseUri);
                    dest = session.createQueue(destName);
                }
            }
            else
            {
                if (dest == null)
                {
                    // Ignore the responseURI
                    String destName = getDestinationName(getUri());
                    dest = session.createTopic(destName);
                }
                // Topics listen on the same topic, instead of temporary queue
                jmsMessage.setJMSReplyTo(dest);
            }
            if (!selector.equals(""))
            {
                jmsMessage.setJMSType(selector);
                // For the topic, the source and destination are important.
                jmsMessage.setStringProperty("Source", MyID);
                String destID = (String) context.getProperty("Destination");
                if (destID == null)
                {
                    destID = responseUri.equalsIgnoreCase("urn:xfire:channel:backchannel") ? "Client" : "Service";
                }
                jmsMessage.setStringProperty("Destination", destID);
            }
            session.createProducer(null).send(dest, jmsMessage);
            log.debug("Sent message: Source ID: " + MyID);
        }
        catch (JMSException e)
        {
            throw new XFireFault("Error sending message", e, XFireFault.SENDER);
        }
    }

    public void onMessage(Message message)
    {
        JMSTransport transport = (JMSTransport) getTransport();
        try
        {
            String text = ((TextMessage) message).getText();
            MessageContext context = new MessageContext();
            context.setId(message.getJMSCorrelationID());
            
            String destName = getDestinationName(getUri());
            if (selector.equals(""))
            {
                context.setService(((JMSTransport) getTransport()).getXFire().getServiceRegistry()
                                .getService(destName));
            }
            else
            {
                context.setService(((JMSTransport) getTransport()).getXFire().getServiceRegistry()
                                .getService(selector));
            }
            context.setProperty(REPLY_TO, message.getJMSReplyTo());
            String srcID = message.getStringProperty("Source");
            log.debug("onMessage -> Source ID: " + srcID + ", Message ID: " + message.getJMSMessageID());

            context.setProperty("Destination", srcID);

            context.setXFire(((JMSTransport) getTransport()).getXFire());

            XMLStreamReader reader = STAXUtils.createXMLStreamReader(new StringReader(text), context);
            InMessage in = new InMessage(reader, getUri());

            receive(context, in);
        }
        catch (JMSException e)
        {
            log.error("Error receiving message " + message, e);
        }
    }

    public void close()
    {
        JMSTransport transport = (JMSTransport) getTransport();
        try
        {
            session.close();
            connection.close();
        }
        catch (JMSException e)
        {
            log.error("Error closing jms connection.", e);
        }
        super.close();
    }
}
