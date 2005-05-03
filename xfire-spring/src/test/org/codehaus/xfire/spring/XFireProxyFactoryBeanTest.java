package org.codehaus.xfire.spring;

import junit.framework.TestCase;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.test.Echo;
import org.codehaus.xfire.test.EchoImpl;
import org.springframework.remoting.RemoteAccessException;

public class XFireProxyFactoryBeanTest
        extends TestCase
{
    private XFireProxyFactoryBean factory;

    protected void setUp()
            throws Exception
    {
        factory = new XFireProxyFactoryBean();
    }

    public void testXFireProxyFactoryBeanWithAccessError()
            throws Exception
    {
        try
        {
            factory.setServiceInterface(EchoImpl.class);
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException ex)
        {
            // expected
        }
        factory.setServiceInterface(Echo.class);
        factory.setServiceUrl("http://localhosta/echo");
        factory.afterPropertiesSet();

        assertTrue("Correct singleton value", factory.isSingleton());
        assertTrue(factory.getObject() instanceof Echo);
        Echo bean = (Echo) factory.getObject();

        try
        {
            bean.echo("test");
            fail("Should have thrown RemoteAccessException");
        }
        catch (RemoteAccessException ex)
        {
            // expected
        }
    }

    public void testXFireProxyFactoryBeanWithCustomProxyFactory()
            throws Exception
    {
        TestXFireProxyFactory proxyFactory = new TestXFireProxyFactory();
        XFireProxyFactoryBean factory = new XFireProxyFactoryBean();
        factory.setServiceInterface(Echo.class);
        factory.setServiceUrl("http://localhosta/testbean");
        factory.setProxyFactory(proxyFactory);
        factory.afterPropertiesSet();
        assertTrue("Correct singleton value", factory.isSingleton());
        assertTrue(factory.getObject() instanceof Echo);
        Echo bean = (Echo) factory.getObject();

        try
        {
            bean.echo("test");
            fail("Should have thrown RemoteAccessException");
        }
        catch (RemoteAccessException ex)
        {
            // expected
        }
    }

    private static class TestXFireProxyFactory
            extends XFireProxyFactory
    {
    }

}