package org.codehaus.xfire.client;

import junit.framework.TestCase;
import org.codehaus.xfire.test.Echo;

public class XFireProxyTest
        extends TestCase
{
    private String url;
    private XFireProxyFactory factory;

    protected void setUp()
            throws Exception
    {
        factory = new XFireProxyFactory();
        url = "http://localhost:8080/services/Echo";
    }


    public void testHandleEquals()
            throws Exception
    {
        Echo echoProxy1 = (Echo) factory.create(Echo.class, url);
        Echo echoProxy2 = (Echo) factory.create(Echo.class, url);
        assertEquals(echoProxy1, echoProxy2);
    }

    public void testHandleHashCode()
            throws Exception
    {
        Echo echoProxy = (Echo) factory.create(Echo.class, url.toString());
        assertTrue(echoProxy.hashCode() != 0);
    }

    public void testHandleToString()
            throws Exception
    {
        Echo echoProxy = (Echo) factory.create(Echo.class, url.toString());
        String result = echoProxy.toString();
        assertNotNull(result);
        assertTrue(result.length() > 0);
        System.out.println("result = " + result);
    }
}