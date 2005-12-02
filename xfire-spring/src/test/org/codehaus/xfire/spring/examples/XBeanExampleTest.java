package org.codehaus.xfire.spring.examples;
// START SNIPPET: load
import junit.framework.TestCase;

import org.codehaus.xfire.service.ServiceRegistry;
import org.xbean.spring.context.ClassPathXmlApplicationContext;

public class XBeanExampleTest
    extends TestCase
{
    public void testLoading()
        throws Exception
    {
        ClassPathXmlApplicationContext context = 
            new ClassPathXmlApplicationContext(new String[] {
                "/org/codehaus/xfire/spring/examples/simple.xml", 
                "/org/codehaus/xfire/spring/xfire.xml" });

        ServiceRegistry reg = (ServiceRegistry) context.getBean("xfire.serviceRegistry");
        assertTrue(reg.hasService("Echo"));
    }
}
// END SNIPPET: load