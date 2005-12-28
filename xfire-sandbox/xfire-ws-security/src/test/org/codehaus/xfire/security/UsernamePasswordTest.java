/*
 * Created on Dec 22, 2005
 */
package org.codehaus.xfire.security;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.spring.AbstractXFireSpringTest;
import org.jdom.Document;
import org.springframework.context.ApplicationContext;
import org.xbean.spring.context.ClassPathXmlApplicationContext;

public class UsernamePasswordTest
    extends AbstractXFireSpringTest
{

    protected ApplicationContext createContext()
    {

        return new ClassPathXmlApplicationContext(
                new String[] { "org/codehaus/xfire/security/services.xml",
                        "org/codehaus/xfire/spring/xfire.xml" });
    }

    public void testService()
        throws Exception
    {
        Service service = getXFire().getServiceRegistry().getService("echo");
        System.out.println(service.getServiceInfo());
        Document document = invokeService("echo","sample-wsse-request.xml");
        assertXPathEquals("/echo/username", "username", document);

    }
}
