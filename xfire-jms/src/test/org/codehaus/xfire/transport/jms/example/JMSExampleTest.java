package org.codehaus.xfire.transport.jms.example;
// START SNIPPET: jms
import java.lang.reflect.Proxy;

import org.codehaus.xfire.client.XFireProxy;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.spring.AbstractXFireSpringTest;
import org.codehaus.xfire.test.Echo;
import org.codehaus.xfire.transport.jms.JMSTransport;
import org.springframework.context.ApplicationContext;
import org.xbean.spring.context.ClassPathXmlApplicationContext;

public class JMSExampleTest
    extends AbstractXFireSpringTest
{
    protected ApplicationContext createContext()
    {
        return new ClassPathXmlApplicationContext(new String[] {
                "/org/codehaus/xfire/transport/jms/example/jms.xml", 
                "/org/codehaus/xfire/spring/xfire.xml" });  
    }

    public void testClient()
            throws Exception
    {
        // Create a ServiceFactory to create the ServiceModel.
        // We need to add the JMSTransport to the list of bindings to create.
        ObjectServiceFactory sf = new ObjectServiceFactory(getTransportManager());
        sf.addSoap11Transport(JMSTransport.BINDING_ID);
        
        // Create the service model
        Service serviceModel = sf.create(Echo.class);
        
        // Create a proxy for the service
        XFireProxyFactory factory = new XFireProxyFactory(getXFire());
        Echo echo = (Echo) factory.create(serviceModel, "jms://Echo");
        
        // Since JMS doesn't really have a concept of anonymous endpoints, we need
        // need to let xfire know what JMS endpoint we should use
        ((XFireProxy) Proxy.getInvocationHandler(echo)).getClient().setEndpointUri("jms://Peer1");
        
        // run the client!
        String resString = echo.echo("hello");
        assertEquals("hello", resString);
    }
}
// END SNIPPET: jms