package org.codehaus.xfire.spring.config;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.BeanInvoker;
import org.codehaus.xfire.service.binding.Invoker;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.spring.AbstractXFireSpringTest;
import org.codehaus.xfire.spring.TestHandler;
import org.codehaus.xfire.spring.XFireConfigLoader;
import org.codehaus.xfire.test.Echo;
import org.codehaus.xfire.test.EchoImpl;
import org.springframework.context.ApplicationContext;
import org.xbean.spring.context.ClassPathXmlApplicationContext;

/**
 * @author tomeks
 *
 */
public class XFireConfigLoaderTest
    extends AbstractXFireSpringTest
{
    public void testConfigLoader()
        throws Exception
    {
        XFireConfigLoader configLoader = new XFireConfigLoader();
        XFire xfire = configLoader.loadConfig(new String[] {"META-INF/xfire/sservices.xml"});
        
        assertNotNull(xfire);
        assertEquals(2, xfire.getInHandlers().size());
        assertTrue(xfire.getInHandlers().get(1) instanceof TestHandler);
        assertEquals(xfire.getOutHandlers().size(),1);
        assertEquals(xfire.getFaultHandlers().size(),1);
        
        Service service = xfire.getServiceRegistry().getService("testservice");
        assertNotNull(service);
        
        SoapVersion version = service.getSoapVersion();
        assertEquals(version,Soap12.getInstance());
        
        assertEquals(2, service.getInHandlers().size());
        Handler testHandler = (Handler) service.getInHandlers().get(1); 
        assertTrue(testHandler instanceof TestHandler);
        assertEquals(testHandler.getAfter().size(),1);
        assertEquals(testHandler.getBefore().size(),2);
        
        assertEquals(service.getOutHandlers().size(),1);
        
        assertEquals(service.getProperty("myKey"),"value");
        assertEquals(service.getProperty("myKey1"),"value1");

        service = xfire.getServiceRegistry().getService("EchoWithJustImpl");
        assertEquals(EchoImpl.class, service.getServiceInfo().getServiceClass());
        
        service = xfire.getServiceRegistry().getService("EchoWithBean");
        Invoker invoker = service.getInvoker();
        assertTrue(invoker instanceof BeanInvoker);
        assertEquals(Echo.class, service.getServiceInfo().getServiceClass());
        
        service = xfire.getServiceRegistry().getService("EchoWithBeanNoServiceClass");
        invoker = service.getInvoker();
        assertTrue(invoker instanceof BeanInvoker);
        assertEquals(EchoImpl.class, service.getServiceInfo().getServiceClass());
        
        service = xfire.getServiceRegistry().getService("EchoWithSchemas");
    }

    protected ApplicationContext createContext()
    {
        return new ClassPathXmlApplicationContext(new String[] {
                "org/codehaus/xfire/spring/xfire.xml", "META-INF/xfire/sservices.xml" });
    }
}
