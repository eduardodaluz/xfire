package org.codehaus.xfire.spring;

import org.codehaus.xfire.AbstractXFireTest;
import org.codehaus.xfire.XFire;
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
        ClassPathResource res = new ClassPathResource("/org/codehaus/xfire/spring/xfire.xml");
        XmlBeanFactory factory = new XmlBeanFactory(res);
        
        Object bean = factory.getBean("xfire");
        assertTrue( bean instanceof XFire );
    }
}
