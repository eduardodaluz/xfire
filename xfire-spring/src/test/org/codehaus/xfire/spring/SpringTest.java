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
public class SpringTest
    extends AbstractXFireTest
{
    public void testSpring()
        throws Exception
    {
        ClassPathResource res = new ClassPathResource("/org/codehaus/xfire/spring/testBeans.xml");
        XmlBeanFactory factory = new XmlBeanFactory(res);
        
        Object bean = factory.getBean(BeanConstants.XFIRE);
        assertTrue( bean instanceof XFire );
        
        ServiceBuilder builder = (ServiceBuilder) factory.getBean(BeanConstants.SERVICE_BUILDER);
        assertNotNull(builder);
        
        Service s = (Service) factory.getBean("echo.service");
    }
}
