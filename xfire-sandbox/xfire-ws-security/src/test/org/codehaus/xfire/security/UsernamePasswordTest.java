/*
 * Created on Dec 22, 2005
 */
package org.codehaus.xfire.security;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.spring.AbstractXFireSpringTest;
import org.jdom.Document;
import org.jdom.output.DOMOutputter;
import org.jdom.output.XMLOutputter;
import org.springframework.context.ApplicationContext;
import org.xbean.spring.context.ClassPathXmlApplicationContext;

public class UsernamePasswordTest
    extends AbstractXFireSpringTest
{
    protected ApplicationContext createContext()
    {
        return new ClassPathXmlApplicationContext(new String[] {
                "org/codehaus/xfire/spring/xfire.xml",
                "org/codehaus/xfire/security/services-attr.xml" });
    }

    public void testService()
        throws Exception
    {
        Service service = getXFire().getServiceRegistry().getService("echo");

        Document document = invokeService("echo", "sample-wsse-request.xml");
        XMLOutputter outputer = new XMLOutputter ();
        outputer.output(document,System.out);
        assertNotNull(document);
        addNamespace("e", "urn:Echo");
        assertValid("//e:echoResponse", document);
    }

    /*public void testUsernameResponse()
        throws Exception
    {

        Document document = invokeService("echo", "sample-wsse-request.xml");
        assertNotNull(document);
        addNamespace("e", "urn:Echo");
        assertValid("//e:echoResponse", document);

    }*/
}