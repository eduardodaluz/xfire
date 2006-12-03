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
import javax.jms.TextMessage;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.addressing.RandomGUID;
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

    private String destName = "";
    
    String MyID = new RandomGUID ().toString();
    //String MyID = java.util.UUID.randomUUID().toString();
      
    public JMSChannel(String uri, JMSTransport transport)
    {
   
        setUri(uri);
        setTransport(transport);
    
        initialize();
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

        String destinationName = getDestinationName();
        if (isTopic)
        {
            destination = session.createTopic(destinationName);
        }
        else
        {
            destination = session.createQueue(destinationName);
        }
        
        consumer = createConsumer();
        
        consumer.setMessageListener(this);
    }

    protected MessageConsumer createConsumer()
        throws JMSException
    {
        // Queues may not have a selector.
        if (!isTopic || selector.equals(""))
        {
            return session.createConsumer(destination);
        }
        else
        {
            return session.createConsumer(destination, createMessageSelector());
        }
    }

    private String createMessageSelector()
    {
        System.out.println("JMSType='" + selector + "' and Source<>'" + MyID + "'");
        return "JMSType='" + selector + "' and Source<>'" + MyID + "')";
    }

    private String getDestinationName()
    {
        return destName;
    }
    
    /**
     * Returns the name of the destination in the URI. For instance, in 
     * jms://Echo or jms://Echo?foo, the destination name would be "Echo".
     * 
     * @param uri
     * @return
     */
    private String getDestinationName(String uri)
    {
        int i = uri.indexOf("://");
        if (i == -1)
            throw new XFireRuntimeException("Invalid JMS URI: " + uri);

        String destName = "";
        int posQMark = uri.indexOf("?", i + 4);
        if (posQMark > 0)
        {
            destName = uri.substring(i + 3, posQMark);
        }
        else
        {
            destName = uri.substring(i + 3);
        }
        
        return destName;
    }
    
    private void initialize()
    {
        String uri = getUri();
        int i = uri.indexOf("://");
        if (i == -1)
            throw new XFireRuntimeException("Invalid JMS URI: " + uri);
        
        int posQMark = uri.indexOf("?", i + 4);
        if (posQMark > 0)
        {
            destName = uri.substring(i + 3, posQMark);
            
            // ok, so there is a question mark. This should be followed by a
            // name=value pair
            // Find the = sign, if not found, assume we're dealing with a queue selector
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
            
            if (selector.equals("."))
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
    }

    public void send(MessageContext context, OutMessage message)
        throws XFireFault
    {
        String msgString = getMessageAsString(context, message);

        try
        {
            Destination dest = ((Destination) context.getProperty(REPLY_TO));
            String responseUri = message.getUri();
            TextMessage jmsMessage = session.createTextMessage();
            jmsMessage.setText(msgString);
            jmsMessage.setJMSCorrelationID(context.getId());

            if (dest == null)
            {
                if (!isTopic)
                {
                    dest = session.createQueue(getDestinationName(responseUri));
                    jmsMessage.setJMSReplyTo(destination);
                }
                else
                {
                    // Ignore the responseURI, and send messages to the topic
                    dest = session.createTopic(getDestinationName());
                    
                    // Topics listen on the same topic, instead of temporary queue
                    jmsMessage.setJMSReplyTo(dest);
                }
            }
            
            if (!selector.equals(""))
            {
                jmsMessage.setJMSType(selector);
                
                // For the topic, the source and destination are important.
                jmsMessage.setStringProperty("Source", MyID);
                
                String destID = (String) context.getProperty("Destination");
                if (destID == null)
                {
                    jmsMessage.setStringProperty("Destination", destID);
                }
            }
            
            session.createProducer(null).send(dest, jmsMessage);
            log.info("Sent message: Source ID: " + MyID  + 
                     " Destination: " + jmsMessage.getStringProperty("Destination") + 
                     " JMSType: " + jmsMessage.getJMSType());
        }
        catch (JMSException e)
        {
            throw new XFireFault("Error sending message", e, XFireFault.SENDER);
        }
    }

    private String getMessageAsString(MessageContext context, OutMessage message)
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
        
        return out.toString();
    }

    public void onMessage(Message message)
    {
        System.out.println("Received message");
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
            System.out.println("onMessage -> Source ID: " + srcID + ", Message ID: " + message.getJMSMessageID());

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
