package org.codehaus.xfire.transport.jms;

import javax.jms.QueueConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.codehaus.xfire.aegis.AbstractXFireAegisTest;

public class AbstractXFireJMSTest
    extends AbstractXFireAegisTest
{
    private QueueConnectionFactory factory;

    private JMSTransport transport;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        factory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");

        transport = (JMSTransport) new JMSTransport(getXFire(), getConnectionFactory());
        
        getXFire().getTransportManager().register(transport);
    }

    public JMSTransport getTransport()
    {
        return transport;
    }

    public QueueConnectionFactory getConnectionFactory()
    {
        return factory;
    }

    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }
}
