package org.codehaus.xfire.spring;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.dom4j.Document;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SpringTest
    extends AbstractXFireTest
{
    XmlBeanFactory factory;

    protected void setUp()
        throws Exception
    {
        ClassPathResource res = new ClassPathResource("/org/codehaus/xfire/spring/SpringTestBeans.xml");
        factory = new XmlBeanFactory(res);

        super.setUp();
    }

    protected XFire getXFire()
    {
        return (XFire) factory.getBean(BeanConstants.XFIRE);
    }

    public void testSpring()
        throws Exception
    {
        ServiceFactory builder = (ServiceFactory) factory.getBean(BeanConstants.SERVICE_BUILDER);
        assertNotNull(builder);
        
        XFireExporter exporter = (XFireExporter) factory.getBean("/Echo");

        assertNotNull(getXFire().getServiceRegistry().getService("Echo"));

        Document response = invokeService("Echo", "/org/codehaus/xfire/spring/echoRequest.xml");
        
        addNamespace("e", "http://spring.xfire.codehaus.org");
        assertValid("//e:echoResponse/e:out[text()='Yo Yo'", response);
    }
}
