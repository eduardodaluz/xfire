package org.codehaus.xfire.spring;

/**
 * @author Arjen Poutsma
 */

import org.codehaus.xfire.service.ServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceComponentTest
        extends AbstractXFireSpringTest
{
    private XFireExporter exporter;

    public void testSpringIntegration()
            throws Exception
    {
        ApplicationContext appContext = getContext();
        
        assertNotNull(appContext.getBean("xfire.serviceFactory"));
        assertNotNull(appContext.getBean("echo"));
        
        ServiceComponent service = (ServiceComponent) appContext.getBean("echoService");
        assertNotNull(service);
        
        assertNotNull(service.getXFireService());
        
        ServiceRegistry reg = (ServiceRegistry) appContext.getBean("xfire.serviceRegistry");
        assertTrue(reg.hasService(service.getXFireService().getName()));
        
        assertNotNull(service.getInHandlers());
    }

    public void testNoIntf()
            throws Exception
    {
        ServiceComponent service = (ServiceComponent) getContext().getBean("echoService");
        assertNotNull(service);
        assertEquals("echoService", service.getXFireService().getName());
    }

    protected ApplicationContext createContext()
    {
        return new ClassPathXmlApplicationContext(new String[]{
                "/org/codehaus/xfire/spring/xfire.xml",
                "/org/codehaus/xfire/spring/serviceComponent.xml"});
    }
}