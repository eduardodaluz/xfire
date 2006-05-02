package org.codehaus.xfire.spring.remoting;

import java.lang.reflect.Proxy;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxy;
import org.codehaus.xfire.server.http.XFireHttpServer;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.test.Echo;
import org.codehaus.xfire.test.EchoImpl;
import org.codehaus.xfire.transport.Channel;
import org.springframework.aop.framework.AopProxy;

public class XFireClientFactoryBeanTest
    extends AbstractXFireAegisTest
{
    private String serviceURL = "http://localhost/Echo";
    private String wsdlUrl;
    private XFireClientFactoryBean factory;
    
    protected void setUp() throws Exception {
        wsdlUrl = getTestFile("src/test/org/codehaus/xfire/spring/remoting/echo.wsdl").toURL().toString();
        
        factory = new XFireClientFactoryBean();
        factory.setServiceClass(Echo.class);
        factory.setWsdlDocumentUrl(wsdlUrl);
        factory.setUrl(serviceURL);
    }
    
    public void testMandatory() 
        throws Exception
    {
        factory = new XFireClientFactoryBean();
        try {
            factory.afterPropertiesSet();
            fail("expected exception without WSDL and service interface");
        } catch (IllegalStateException e) {
            // what we expect
        }

        factory = new XFireClientFactoryBean();
        try {
            factory.setServiceClass(Echo.class);
            factory.afterPropertiesSet();
            fail("expected exception without WSDL");
        } catch (IllegalStateException e) {
            // what we expect since WSDL is required
        }

        factory = new XFireClientFactoryBean();
        try {
            factory.setWsdlDocumentUrl("test");
            factory.afterPropertiesSet();
            fail("expected exception without service interface");
        } catch (IllegalStateException e) {
            // what we expect since interface is required
        }
    }
    
    public void testDefaults()
        throws Exception
    {
        assertTrue("expected lookupServiceOnStartup default to be true", factory.getLookupServiceOnStartup());
        assertNull("default username must be null", factory.getUsername());
        assertNull("default password must be null", factory.getPassword());
        assertEquals("default service factory is wrong type", ObjectServiceFactory.class, factory.getServiceFactory().getClass());
        assertEquals("default type (before afterPropertiesSet) is not interface", Echo.class, factory.getObjectType());

        assertNull("default service name must be null", factory.getServiceName());
        assertNull("default namespaceUri must be null", factory.getNamespaceUri());
    }
    
    public void testXFireProxyFactoryBeanLoadOnStartup()
        throws Exception
    {
        factory.afterPropertiesSet();
        
        Class objectType = factory.getObjectType();
        assertTrue("object created by factory does not implement interface", Echo.class.isAssignableFrom(objectType));

        Echo obj = (Echo)factory.getObject(); 
        Object handler = Proxy.getInvocationHandler(obj);
        Class handlerClass = handler.getClass();
        assertTrue("factory created own proxy: " + handlerClass, XFireProxy.class.isAssignableFrom(handlerClass));        
        XFireProxy fireProxy = (XFireProxy)handler;
        checkAuth(fireProxy, null, null);
        Client c = fireProxy.getClient();
        assertEquals("wrong service URL", serviceURL, c.getUrl());
    }    
    
    public void testXFireProxyFactoryBeanNoLoadOnStartup()
        throws Exception
    {
        factory.setLookupServiceOnStartup(false);
        factory.afterPropertiesSet();
        
        Class objectType = factory.getObjectType();
        assertTrue("object created by factory does not implement interface", Echo.class.isAssignableFrom(objectType));
        Echo obj = (Echo)factory.getObject(); 
        Object handler = Proxy.getInvocationHandler(obj);
        Class handlerClass = handler.getClass();
        assertTrue("factory did not create own proxy: " + handlerClass, AopProxy.class.isAssignableFrom(handlerClass));
        
        assertEquals("Wrong uninit toString() for proxy", 
                     "Un-initialized XFire client proxy for: interface org.codehaus.xfire.test.Echo at: " + serviceURL, 
                     obj.toString());
    }
    
    public void testAuthentication() 
        throws Exception
    {
        String expectedUsername = "fried";
        String expectedPassword = "hoeben";
        factory.setUsername(expectedUsername);
        factory.setPassword(expectedPassword);
        factory.afterPropertiesSet();

        Echo obj = (Echo)factory.getObject(); 
        
        XFireProxy handler = (XFireProxy)Proxy.getInvocationHandler(obj);
        checkAuth(handler, expectedUsername, expectedPassword);
    }

    private void checkAuth(XFireProxy handler, String expectedUsername, String expectedPassword)
    {
        Client client = handler.getClient();
        String username = (String)client.getProperty(Channel.USERNAME);
        assertEquals("wrong username", expectedUsername, username);
        String password = (String)client.getProperty(Channel.PASSWORD);
        assertEquals("wrong password", expectedPassword, password);
    }
    
    public void testSetWSDLProperties()
        throws Exception
    {
        // first ensure we have a WSDL to parse
        super.setUp();
        ServiceFactory serverFact = getServiceFactory();
     
        XFireHttpServer server = new XFireHttpServer(getXFire());
        server.setPort(8191);
        server.start();
        
        Service service = serverFact.create(Echo.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);

        getServiceRegistry().register(service);

        // now create a special factory that will actually use the created WSDL
        factory = new XFireClientFactoryBean();
        factory.setServiceClass(Echo.class);
        factory.setWsdlDocumentUrl("http://localhost:8191/Echo?wsdl");
        factory.afterPropertiesSet();
        assertEquals("Wrong serviceName from WSDL", "Echo", factory.getServiceName());
        assertEquals("Wrong namespaceUri from WSDL", "http://test.xfire.codehaus.org", factory.getNamespaceUri());
        
        Echo echo = (Echo) factory.getObject();
        assertEquals("hi", echo.echo("hi"));
        
        server.stop();
    }
 
}