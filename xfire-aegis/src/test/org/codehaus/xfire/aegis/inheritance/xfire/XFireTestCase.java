package org.codehaus.xfire.aegis.inheritance.xfire;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.aegis.inheritance.ws1.BeanA;
import org.codehaus.xfire.aegis.inheritance.ws1.BeanB;
import org.codehaus.xfire.aegis.inheritance.ws1.WS1;
import org.codehaus.xfire.aegis.inheritance.ws1.impl.WS1Impl;
import org.codehaus.xfire.aegis.inheritance.ws2.WS2;
import org.codehaus.xfire.aegis.inheritance.ws2.common.ParentBean;
import org.codehaus.xfire.aegis.inheritance.ws2.common.pack1.ContentBean1;
import org.codehaus.xfire.aegis.inheritance.ws2.common.pack2.ContentBean2;
import org.codehaus.xfire.aegis.inheritance.ws2.impl.WS2Impl;
import org.codehaus.xfire.server.http.XFireHttpServer;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;

/**
 * <br/>
 * 
 * @author xfournet
 */
public class XFireTestCase
    extends AbstractXFireTest
{
    private XFireHelper m_helper;

    private WS1 m_remoteWS1;

    private WS1 m_localWS1;

    private WS2 m_remoteWS2;

    private WS2 m_localWS2;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        String url = "http://localhost:8080";

        assertNotNull("Property 'xfiretestcase.url' is not set !", url);

        XFireHelper xFireHelper = new XFireHelper();

        m_localWS1 = new WS1Impl();
        m_remoteWS1 = (WS1) xFireHelper.createClientProxy(xFireHelper.createServiceWS1(), url);

        m_localWS2 = new WS2Impl();
        m_remoteWS2 = (WS2) xFireHelper.createClientProxy(xFireHelper.createServiceWS2(), url);

        startServer();
    }

    protected void tearDown()
        throws Exception
    {
        xFireHttpServer.stop();
        super.tearDown();
    }

    XFireHttpServer xFireHttpServer;

    public void startServer()
        throws Exception
    {
        // Create WS
        m_helper = new XFireHelper(getXFire());
        m_helper.registerService(m_helper.createServiceWS1(), new WS1Impl());
        m_helper.registerService(m_helper.createServiceWS2(), new WS2Impl());

        // Start Jetty server
        xFireHttpServer = new XFireHttpServer(m_helper.getXfire());
        xFireHttpServer.setPort(8080);
        xFireHttpServer.start();
    }

    protected void assertEquals(Object[] expected, Object[] result)
    {
        if (!Arrays.equals(expected, result))
        {
            fail("Expected : " + Arrays.asList(expected) + " ; result : " + Arrays.asList(result));
        }
    }

    public void testExplicitInheritance()
    {
        assertEquals(m_localWS1.getBeanA(), m_remoteWS1.getBeanA());
        assertEquals(m_localWS1.getBeanB(), m_remoteWS1.getBeanB());
    }

    public void testNonExplicitInheritance()
    {
        assertEquals(m_localWS1.getBean("a"), m_localWS1.getBean("a"));
        assertEquals(m_localWS1.getBean("a"), m_remoteWS1.getBean("a"));
        assertEquals(new BeanB(), new BeanB());
        assertEquals(m_localWS1.getBean("b"), m_localWS1.getBean("b"));
        assertEquals(m_localWS1.getBean("b"), m_remoteWS1.getBean("b"));
        assertEquals(m_localWS1.getBean("c"), m_remoteWS1.getBean("c"));
        assertEquals(m_localWS1.getBean("d"), m_remoteWS1.getBean("d"));
    }

    public void testChildInheritance()
    {
        assertEquals(m_localWS1.getRootBean("a"), m_remoteWS1.getRootBean("a"));
        assertEquals(m_localWS1.getRootBean("b"), m_remoteWS1.getRootBean("b"));
        assertEquals(m_localWS1.getRootBean("c"), m_remoteWS1.getRootBean("c"));
        assertEquals(m_localWS1.getRootBean("d"), m_remoteWS1.getRootBean("d"));
    }

    public void testArrayWithInheritance()
    {
//        assertEquals(m_localWS1.listBeans(), m_remoteWS1.listBeans());
        assertEquals(m_localWS1.listRootBeans(), m_remoteWS1.listRootBeans());
//        assertEquals(m_localWS1.getResultBean(), m_remoteWS1.getResultBean());
    }

    public void testMapInheritance()
    {
        BeanA beanA = new BeanA();
        BeanB beanB = new BeanB();
        
        Map m = new HashMap();
        m.put(beanA, beanB);
        
        Map response = m_remoteWS1.echoMap(m);
        assertEquals(1, response.size());
        
        Object objB = response.get(beanA);
        assertEquals(objB, beanB);
    }
    
    public void testInheritedException()
    {
        Throwable localThrowable;
        Throwable remoteThrowable;

        // test base exception
        try
        {
            m_localWS1.throwException(false);
            localThrowable = null;
        }
        catch (Throwable t)
        {
            localThrowable = t;
        }
        assertNotNull(localThrowable);

        try
        {
            m_remoteWS1.throwException(false);
            remoteThrowable = null;
        }
        catch (Throwable t)
        {
            remoteThrowable = t;
        }
        assertNotNull(remoteThrowable);

        assertEquals(localThrowable, remoteThrowable);

        // test inherited exception
        try
        {
            m_localWS1.throwException(true);
            localThrowable = null;
        }
        catch (Throwable t)
        {
            localThrowable = t;
        }
        assertNotNull(localThrowable);

        try
        {
            m_remoteWS1.throwException(true);
            remoteThrowable = null;
        }
        catch (Throwable t)
        {
            remoteThrowable = t;
        }
        assertNotNull(remoteThrowable);

        assertEquals(localThrowable, remoteThrowable);
    }

    public void testMixedPackageChildInheritance()
        throws Exception
    {
        assertEquals(m_localWS2.getParentBean("X"), m_remoteWS2.getParentBean("X"));
        assertEquals(m_localWS2.getParentBean("Y"), m_remoteWS2.getParentBean("Y"));

        String baseId = System.currentTimeMillis() + "-";
        ParentBean parentBean;

        parentBean = new ParentBean(baseId + "A", new ContentBean1("data1-A"));
        m_localWS2.putParentBean(parentBean);
        m_remoteWS2.putParentBean(parentBean);
        assertEquals(m_localWS2.getParentBean(parentBean.getId()), m_remoteWS2
                .getParentBean(parentBean.getId()));

        parentBean = new ParentBean(baseId + "B", new ContentBean2("data1-B", "content2-B"));
        m_localWS2.putParentBean(parentBean);
        m_remoteWS2.putParentBean(parentBean);
        assertEquals(m_localWS2.getParentBean(parentBean.getId()), m_remoteWS2
                .getParentBean(parentBean.getId()));
    }

    public void testMixedPackageException()
    {
        Throwable localThrowable;
        Throwable remoteThrowable;

        // test AlreadyExists exception
        try
        {
            m_localWS2.putParentBean(new ParentBean("X", null));
            localThrowable = null;
        }
        catch (Throwable t)
        {
            localThrowable = t;
        }
        assertNotNull(localThrowable);

        try
        {
            m_remoteWS2.putParentBean(new ParentBean("X", null));
            remoteThrowable = null;
        }
        catch (Throwable t)
        {
            remoteThrowable = t;
        }
        assertNotNull(remoteThrowable);

        assertEquals(localThrowable, remoteThrowable);

        // test NotFound exception
        try
        {
            m_localWS2.getParentBean("Z");
            localThrowable = null;
        }
        catch (Throwable t)
        {
            localThrowable = t;
        }
        assertNotNull(localThrowable);

        try
        {
            m_remoteWS2.getParentBean("Z");
            remoteThrowable = null;
        }
        catch (Throwable t)
        {
            remoteThrowable = t;
        }
        assertNotNull(remoteThrowable);

        assertEquals(localThrowable, remoteThrowable);
    }

    public void testWSDLDocument()
        throws Exception
    {
        Document wsdl = getWSDLDocument("ws1");
        assertValid("//wsdl:types/xsd:schema/xsd:complexType[@name='ArrayOfBeanD']", wsdl);
    }
}