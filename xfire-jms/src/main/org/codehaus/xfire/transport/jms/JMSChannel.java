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
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.transport.AbstractChannel;
import org.codehaus.xfire.util.STAXUtils;

public class JMSChannel 
    extends AbstractChannel implements MessageListener
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
        
        String queueName = getQueueName(getUri());

        queue = session.createQueue(queueName);

        consumer = session.createConsumer(queue);
        consumer.setMessageListener(this);
    }

    private String getQueueName(String uri)
    {
        int i = uri.indexOf("://");
        if (i == -1) throw new XFireRuntimeException("Invalid JMS URI: " + getUri());
        
        String queueName = uri.substring(i+3);
        return queueName;
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

            TemporaryQueue reply = session.createTemporaryQueue();
            session.createConsumer(reply).setMessageListener(this);
            
            TextMessage jmsMessage = session.createTextMessage();
            jmsMessage.setText(out.toString());
            jmsMessage.setJMSReplyTo(reply);
            
            if (context.getId() != null)
                jmsMessage.setJMSCorrelationID(context.getId());
            
            if (dest == null)
            {
                dest = session.createQueue(getQueueName(responseUri));
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
            
            MessageContext context = new MessageContext(); 
            context.setService(((JMSTransport) getTransport()).getXFire().getServiceRegistry().getService(getQueueName(getUri())));
            context.setProperty(REPLY_TO, message.getJMSReplyTo());
            context.setXFire(((JMSTransport) getTransport()).getXFire());
            context.setId(message.getJMSCorrelationID());
            
            XMLStreamReader reader = STAXUtils.createXMLStreamReader(new StringReader(text), context);
            InMessage in = new InMessage(reader, getUri());
    
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
            log.error("Error closing jms connection.", e);
        }
        
        super.close();
    }
}
