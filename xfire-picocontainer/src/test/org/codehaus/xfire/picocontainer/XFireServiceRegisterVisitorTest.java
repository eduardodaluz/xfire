package org.codehaus.xfire.picocontainer;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.codehaus.xfire.picocontainer.test.PicoXFireTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.invoker.Invoker;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;

public class XFireServiceRegisterVisitorTest
    extends PicoXFireTest
{

    public int instanceCount = 0;

    public int callCount = 0;

    private XFireServiceRegisterVisitor xfireVisitor;

    public void setUp()
        throws Exception
    {
        super.setUp();
        instanceCount = 0;
        callCount = 0;

        // Create using PicoObjectServiceFactory and put the Service into the
        // pico.
        ServiceFactory sf = getServiceFactory();
        Service ms = sf.create(DummyServiceThatCounts.class);
        ms.setName(new QName("test"));
        getPico().registerComponentInstance(ms);

        // Register XFireServiceRegisterVisitor and its dependency into the
        // pico.
        getPico().registerComponentInstance(ServiceRegistry.class, getXFire().getServiceRegistry());
        getPico().registerComponentImplementation(XFireServiceRegisterVisitor.class);

        // Get XFireServiceRegisterVisitor and execute it, now out last created
        // Service must be registred.
        xfireVisitor = (XFireServiceRegisterVisitor) getPico()
                .getComponentInstanceOfType(XFireServiceRegisterVisitor.class);
        getPico().accept(xfireVisitor);
    }

    public void testCachedServiceObject()
        throws Exception
    {
        // Put DummyServiceThatCounts and its dependency into the pico. Its
        // going to be cached, just one instance for container.
        getPico().registerComponent(new CachingComponentAdapter(
                new ConstructorInjectionComponentAdapter(DummyServiceThatCounts.class,
                        DummyServiceThatCounts.class)));
        getPico().registerComponentInstance(this);

        // Execute it 3 times.
        ServiceRegistry sr = (ServiceRegistry) getPico()
                .getComponentInstance(ServiceRegistry.class);
        assertNotNull(sr);

        Service endpoint = sr.getService("test");
        assertNotNull(endpoint);

        Invoker invoker = endpoint.getInvoker();
        assertNotNull(invoker);

        Method method = DummyServiceThatCounts.class.getMethod("theMethod", new Class[] {});
        assertNotNull(method);

        invoker.invoke(method, new Object[] {}, null);
        invoker.invoke(method, new Object[] {}, null);
        invoker.invoke(method, new Object[] {}, null);

        // Assert it
        assertEquals(1, instanceCount);
        assertEquals(3, callCount);
    }

    public void testNotCachedServiceObject()
        throws Exception
    {
        // Put DummyServiceThatCounts and its dependency into the pico. Its
        // going to NOT be cached, one instance by request.
        getPico().registerComponent(new ConstructorInjectionComponentAdapter(
                DummyServiceThatCounts.class, DummyServiceThatCounts.class));
        getPico().registerComponentInstance(this);

        // Execute it 3 times.
        ServiceRegistry sr = (ServiceRegistry) getPico()
                .getComponentInstance(ServiceRegistry.class);
        assertNotNull(sr);

        Service endpoint = sr.getService("test");
        assertNotNull(endpoint);

        Invoker invoker = endpoint.getInvoker();
        assertNotNull(invoker);

        Method method = DummyServiceThatCounts.class.getMethod("theMethod", new Class[] {});
        assertNotNull(method);

        invoker.invoke(method, new Object[] {}, null);
        invoker.invoke(method, new Object[] {}, null);
        invoker.invoke(method, new Object[] {}, null);

        // Assert it
        assertEquals(3, instanceCount);
        assertEquals(3, callCount);
    }

    
    public class DummyServiceThatCounts
    {

        private final XFireServiceRegisterVisitorTest test;

        public DummyServiceThatCounts(XFireServiceRegisterVisitorTest test)
        {
            this.test = test;
            this.test.instanceCount++;
        }

        public void theMethod()
        {
            test.callCount++;
            return;
        }
    }

}
