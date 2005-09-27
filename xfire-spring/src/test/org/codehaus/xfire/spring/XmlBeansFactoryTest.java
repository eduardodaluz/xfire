package org.codehaus.xfire.spring;

/**
 * @author Arjen Poutsma
 */

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class XmlBeansFactoryTest
        extends AbstractXFireSpringTest
{
    public void testServiceFactory()
            throws Exception
    {
        ApplicationContext appContext = getContext();
        
        appContext.getBean("xfire.xmlbeansServiceFactory");
    }

    protected ApplicationContext createContext()
    {
        return new ClassPathXmlApplicationContext(new String[]{
                "/org/codehaus/xfire/spring/xfire.xml",
                "/org/codehaus/xfire/spring/xfireXmlBeans.xml"});
    }
}