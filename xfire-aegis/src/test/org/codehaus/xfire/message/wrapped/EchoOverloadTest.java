package org.codehaus.xfire.message.wrapped;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.services.EchoOverload;
import org.codehaus.xfire.services.EchoOverloadImpl;
import org.jdom.Document;

public class EchoOverloadTest
    extends AbstractXFireAegisTest
{
    private Service service;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        service = getServiceFactory().create(EchoOverload.class, "EchoOverload", "urn:xfire:echo", null);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoOverloadImpl.class);
        
        getServiceRegistry().register(service);
    }

    public void testService() throws Exception
    {
        Document response = invokeService("EchoOverload", "echoOverload.xml");

        addNamespace("e", "urn:xfire:echo");
        
        assertValid("//e:out[text()='yo']", response);
        
        response = invokeService("EchoOverload", "echoOverload2.xml");

        assertValid("//e:out[text()='yoyo']", response);
    }
    
    public void testClient() throws Exception
    {
        XFireProxyFactory factory = new XFireProxyFactory(getXFire());
        EchoOverload echo = (EchoOverload) factory.create(service, "xfire.local://EchoOverload");

        assertEquals("yo", echo.echo("yo"));
        assertEquals("yoyo", echo.echo("yo", "yo"));
    }
}
