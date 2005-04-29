package org.codehaus.xfire.picocontainer;

import java.lang.reflect.Method;

import junit.framework.TestCase;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.binding.Invoker;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.transport.TransportManager;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;

public class XFireServiceRegisterVisitorTest
        extends TestCase
{

    public int instanceCount = 0;

    public int callCount = 0;

    private MutablePicoContainer pico;

    private XFireServiceRegisterVisitor xfireVisitor;

    protected void setUp()
            throws Exception
    {
        super.setUp();
        instanceCount = 0;
        callCount = 0;

        // Creates picocontainer and put defaults xfire components there.
        pico = new DefaultPicoContainer();
        populateXFireComponents(pico);

        // Register our service mocked directly.
        ServiceFactory sf = (ServiceFactory) pico.getComponentInstance(ServiceFactory.class);
        Service ms = sf.create(DummyServiceThatCounts.class);
        ms.setName("test");
        pico.registerComponentInstance(ms);

        // Register container's services
        xfireVisitor = (XFireServiceRegisterVisitor) pico
                .getComponentInstance(XFireServiceRegisterVisitor.class);
        pico.accept(xfireVisitor);
    }

    public void testServiceRegistrationByComponentAdaptor()
            throws Exception
    {

    }

    public void testCachedServiceObject()
            throws Exception
    {
        // Create a child container
        MutablePicoContainer picoChild = new DefaultPicoContainer(pico);
        picoChild.registerComponentInstance(this);
        picoChild.registerComponentImplementation(DummyServiceThatCounts.class);

        // Update pico reference
        xfireVisitor.setPicocontainer(picoChild);

        // Execute it 3 times.
        ServiceRegistry sr = (ServiceRegistry) pico.getComponentInstance(ServiceRegistry.class);
        assertNotNull(sr);

        Service s = sr.getService("test");
        assertNotNull(s);

        Invoker invoker = s.getInvoker();
        assertNotNull(invoker);

        Method method = DummyServiceThatCounts.class.getMethod("theMethod", new Class[]{});
        assertNotNull(method);

        invoker.invoke(method, new Object[]{}, null);
        invoker.invoke(method, new Object[]{}, null);
        invoker.invoke(method, new Object[]{}, null);

        // Just once
        assertEquals(1, instanceCount);

        assertEquals(3, callCount);
    }

    public void testNotCachedServiceObject()
            throws Exception
    {
        // Create a child container
        MutablePicoContainer picoChild = new DefaultPicoContainer(pico);
        picoChild.registerComponentInstance(this);
        picoChild.registerComponent(new ConstructorInjectionComponentAdapter(DummyServiceThatCounts.class,
                                                                             DummyServiceThatCounts.class));

        // Update pico reference
        xfireVisitor.setPicocontainer(picoChild);

        // Execute it 3 times.
        ServiceRegistry sr = (ServiceRegistry) pico.getComponentInstance(ServiceRegistry.class);
        assertNotNull(sr);

        Service s = sr.getService("test");
        assertNotNull(s);

        Invoker invoker = s.getInvoker();
        assertNotNull(invoker);

        Method method = DummyServiceThatCounts.class.getMethod("theMethod", new Class[]{});
        assertNotNull(method);

        invoker.invoke(method, new Object[]{}, null);
        invoker.invoke(method, new Object[]{}, null);
        invoker.invoke(method, new Object[]{}, null);

        // Now its 3 times!
        assertEquals(3, instanceCount);

        assertEquals(3, callCount);
    }

    private void populateXFireComponents(MutablePicoContainer pico)
            throws Exception
    {
        XFire xfire = XFireFactory.newInstance().getXFire();
        pico.registerComponentInstance(XFire.class, xfire);
        pico.registerComponentImplementation(TransportManager.class,
                                             DefaultTransportManagerDelegator.class);
        pico.registerComponentImplementation(ServiceRegistry.class,
                                             DefaultServiceRegistryDelegator.class);
        pico.registerComponentImplementation(ServiceFactory.class, ObjectServiceFactory.class);
        pico.registerComponentImplementation(XFireServiceRegisterVisitor.class,
                                             XFireServiceRegisterVisitor.class);
        pico.registerComponentImplementation(TypeMappingRegistry.class,
                                             DefaultTypeMappingRegistry.class,
                                             new Parameter[]{new ConstantParameter(new Boolean(true))});
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
