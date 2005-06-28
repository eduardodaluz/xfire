package org.codehaus.xfire.transport.jms;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.transport.AbstractSoapChannel;
import org.codehaus.xfire.util.STAXUtils;

public class JMSChannel 
    extends AbstractSoapChannel implements MessageListener
{
    public static final String REPLY_TO = "jms.replyTo";
    public static final String JMS_URI = "urn:codehaus:xfire:jms";
  
    private static final Log log = LogFactory.getLog(JMSChannel.class);
    private Session session;
    private Connection connection;
    private Queue queue;
    private MessageConsumer consumer;

    public JMSChannel(String uri, JMSTransport transport)
    {
        setUri(uri);
        setTransport(transport);
    }

    public void open() throws JMSException
    {
        if (session != null)
            return;

        JMSTransport transport = (JMSTransport) getTransport();
        connection = transport.getConnectionFactory().createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        queue = session.createQueue(getUri());

        consumer = session.createConsumer(queue);
        consumer.setMessageListener(this);
    }

    public void send(MessageContext context, OutMessage message)
        throws XFireFault
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, message.getEncoding());

        sendSoapMessage(message, writer, context);
        
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

            TemporaryQueue reply = session.createTemporaryQueue();
            session.createConsumer(reply).setMessageListener(this);
            
            TextMessage jmsMessage = session.createTextMessage();
            jmsMessage.setText(out.toString());
            jmsMessage.setJMSReplyTo(reply);

            if (dest == null)
            {
                dest = session.createQueue(responseUri);
            }

            session.createProducer(null).send(dest, jmsMessage);
        }
        catch(JMSException e)
        {
            throw new XFireFault("Error sending message", e, XFireFault.SENDER);
        }
    }

    public void onMessage(Message message)
    {
        try
        {
            String text = ((TextMessage)message).getText();
            XMLStreamReader reader = STAXUtils.createXMLStreamReader(new StringReader(text));
            InMessage in = new InMessage(reader, getUri());
    
            MessageContext context = new MessageContext(); 
            context.setService(getService());
            context.setProperty(REPLY_TO, message.getJMSReplyTo());
    
            receive(context, in);
        }
        catch(JMSException e)
        {
          log.error("Error receiving message " + message, e);
        }
    }

    public void close()
    {
        try
        {
            session.close();
            connection.close();
        }
        catch(JMSException e)
        {
            log.error("Error closing jms", e);
        }
    }
}
