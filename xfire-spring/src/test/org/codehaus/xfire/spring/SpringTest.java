package org.codehaus.xfire.spring;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.handler.SoapHandler;
import org.codehaus.xfire.message.ObjectServiceHandler;
import org.codehaus.xfire.service.object.DefaultObjectService;
import org.codehaus.xfire.service.object.ServiceBuilder;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.dom4j.Document;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SpringTest
    extends AbstractXFireTest
{
    BeanFactory factory;

    protected void setUp()
        throws Exception
    {
        ClassPathResource res = new ClassPathResource("/org/codehaus/xfire/spring/testBeans.xml");
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
        ServiceBuilder builder = (ServiceBuilder) factory.getBean(BeanConstants.SERVICE_BUILDER);
        assertNotNull(builder);
        
        DefaultObjectService s = (DefaultObjectService) builder.create(Echo.class, 
                                                                       "Echo", 
                                                                       "urn:Echo", 
                                                                       Soap11.getInstance(),
                                                                       SoapConstants.STYLE_WRAPPED,
                                                                       SoapConstants.USE_LITERAL);
        
        s.setServiceHandler(new SoapHandler(new ObjectServiceHandler(new BeanInvoker(factory, "echo"))));
        
        assertNotNull(getXFire().getServiceRegistry().getService("Echo"));

        Document response = invokeService("Echo", "/org/codehaus/xfire/spring/echoRequest.xml");
        
        addNamespace("e", "urn:Echo");
        assertValid("//e:echoResponse/e:out[text()='Yo Yo'", response);
    }
}
