package org.codehaus.xfire.spring;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.object.ServiceBuilder;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SpringServletTest
    extends AbstractXFireTest
{
    public void testSpring()
        throws Exception
    {
        ClassPathResource res = new ClassPathResource("/org/codehaus/xfire/spring/xfire.xml");
        XmlBeanFactory factory = new XmlBeanFactory(res);
        
        Object bean = factory.getBean("xfire");
        assertTrue( bean instanceof XFire );
        
        ServiceBuilder builder = (ServiceBuilder) factory.getBean("xfire.serviceBuilder");
        assertNotNull(builder);
        
        Service s = (Service) factory.getBean("echo.service");
        
    }
}
