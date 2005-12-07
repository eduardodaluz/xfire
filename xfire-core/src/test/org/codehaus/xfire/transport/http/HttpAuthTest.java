package org.codehaus.xfire.transport.http;

import java.lang.reflect.Proxy;
import java.net.MalformedURLException;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxy;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.Channel;
import org.jdom.Element;
import org.mortbay.http.HashUserRealm;
import org.mortbay.http.HttpContext;
import org.mortbay.http.SecurityConstraint;
import org.mortbay.http.handler.SecurityHandler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.util.InetAddrPort;

public class HttpAuthTest
    extends AbstractXFireTest
{
    private Service service;
    private Server httpServer;

    public void setUp() throws Exception
    {
        super.setUp();
        
        httpServer = new Server();
        httpServer.addListener(new InetAddrPort(8191));
        
        HttpContext context = httpServer.getContext("/");
        context.setRequestLog(null);
        
        context.addHandler(new SecurityHandler());
        
        ServletHandler handler = new ServletHandler();
        handler.addServlet("XFireServlet", "/*", XFireServlet.class.getName());
        
        context.addHandler(handler);
        
        HashUserRealm userRealm = new HashUserRealm();
        userRealm.put("user", "pass");
        userRealm.addUserToRole("user", "role");

        assertNotNull(userRealm.authenticate("user", "pass", null));
        
        context.setRealm(userRealm);
        SecurityConstraint constraint = new SecurityConstraint("*", "role");
        constraint.addMethod("POST");
        context.addSecurityConstraint("/Echo", constraint);
        
        httpServer.start();
        
        service = getServiceFactory().create(Echo.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);
        
        service.setBindingProvider(new MessageBindingProvider());

        getServiceRegistry().register(service);
    }

    protected XFire getXFire()
    {
        XFireFactory factory = XFireFactory.newInstance();
        return factory.getXFire();
    }

    protected void tearDown()
        throws Exception
    {
        httpServer.stop();
        
        super.tearDown();
    }

    public void testProxy() throws MalformedURLException, XFireFault
    {
        Echo echo = (Echo) new XFireProxyFactory().create(service, "http://localhost:8191/Echo");
        
        Client client = ((XFireProxy) Proxy.getInvocationHandler(echo)).getClient();
        client.setProperty(Channel.USERNAME, "user");
        client.setProperty(Channel.PASSWORD, "pass");
        
        Element root = new Element("root", "a", "urn:a");
        root.addContent("hello");
        
        Element e = echo.echo(root);
        
        assertEquals(root.getName(), e.getName());
    }
}
