package org.codehaus.xfire.spring.remoting;

import org.codehaus.xfire.spring.AbstractXFireSpringTest;
import org.codehaus.xfire.test.Echo;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class XFireProxyFactoryBeanTest
    extends AbstractXFireSpringTest
{
    public void testXFireProxyFactoryBeanWithAccessError()
        throws Exception
    {
        Object client = getContext().getBean("echoClient");
        assertNotNull(client);

        Echo echo = (Echo) client;
        String response = echo.echo("bleh");
        assertNotNull(response);
        assertEquals("bleh", response);
    }
    
    protected ApplicationContext createContext()
    {
        return new ClassPathXmlApplicationContext(new String[] {
                "/org/codehaus/xfire/spring/xfire.xml",
                "/org/codehaus/xfire/spring/remoting/exporterTest.xml",
                "/org/codehaus/xfire/spring/remoting/proxyFactoryTest.xml" });
    }
}