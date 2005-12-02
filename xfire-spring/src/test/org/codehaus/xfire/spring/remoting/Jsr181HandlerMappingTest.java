package org.codehaus.xfire.spring.remoting;

import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.spring.AbstractXFireSpringTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Jsr181HandlerMappingTest
        extends AbstractXFireSpringTest
{
  
    public void testNoAnnotation()
            throws Exception
    {
        ServiceRegistry reg =getXFire().getServiceRegistry();
        assertEquals(1, reg.getServices().size());
        assertTrue(reg.hasService("EchoImpl"));
    }

    protected ApplicationContext createContext()
    {
        return new ClassPathXmlApplicationContext(new String[] {
                "/org/codehaus/xfire/spring/xfire.xml",
                "/org/codehaus/xfire/spring/remoting/handlerMapping.xml"
        });
    }
    
}