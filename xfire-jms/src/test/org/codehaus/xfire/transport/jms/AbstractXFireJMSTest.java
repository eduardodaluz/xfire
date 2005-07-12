package org.codehaus.xfire.transport.jms;

import javax.jms.QueueConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.activemq.broker.BrokerContainer;
import org.activemq.broker.impl.BrokerContainerImpl;
import org.activemq.store.vm.VMPersistenceAdapter;
import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.soap.SoapTransport;

public class AbstractXFireJMSTest
    extends AbstractXFireAegisTest
{
    BrokerContainer container;

    private QueueConnectionFactory factory;

    private JMSTransport transport;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        container = new BrokerContainerImpl();
        container.addConnector("vm://localhost");
        container.setPersistenceAdapter(new VMPersistenceAdapter());
        
        container.start();

        Context context = new InitialContext();

        factory = (QueueConnectionFactory) context.lookup("QueueConnectionFactory");

        transport = (JMSTransport)
            SoapTransport.createSoapTransport(new JMSTransport(getXFire(), getConnectionFactory()));
        
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
        container.stop();

        super.tearDown();
    }
}
