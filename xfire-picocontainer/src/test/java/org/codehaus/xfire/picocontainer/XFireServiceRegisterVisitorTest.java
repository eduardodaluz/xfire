package org.codehaus.xfire.picocontainer;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.codehaus.xfire.service.MessageService;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.object.Invoker;
import org.codehaus.xfire.service.object.ObjectService;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;

public class XFireServiceRegisterVisitorTest extends TestCase {

    public int instanceCount = 0;
    public int callCount = 0;

    private MutablePicoContainer pico;
    private XFireServiceRegisterVisitor xfireVisitor;

    protected void setUp() throws Exception {
        super.setUp();
        this.instanceCount = 0;
        this.callCount = 0;

        // Creates picocontainer and put defaults xfire components there.
        this.pico = new DefaultPicoContainer();
        (new DefaultXFireComponentsContainerComposer()).composeContainer(this.pico, null);

        // Register our mocked service pojo.
        ServiceFactory sf = (ServiceFactory) this.pico.getComponentInstance(ServiceFactory.class);
        MessageService ms = (MessageService) sf.create(DummyServiceThatCounts.class);
        ms.setName("test");
        this.pico.registerComponentInstance(ms);

        // Register container's services
        this.xfireVisitor = (XFireServiceRegisterVisitor) this.pico.getComponentInstance(XFireServiceRegisterVisitor.class);
        this.pico.accept(xfireVisitor);
    }

    public void testCachedServiceObject() throws Exception {
        // Create a child container
        MutablePicoContainer picoChild = new DefaultPicoContainer(pico);
        picoChild.registerComponentInstance(this);
        picoChild.registerComponentImplementation(DummyServiceThatCounts.class);

        // Update pico reference
        this.xfireVisitor.setPicocontainer(picoChild);

        // Execute it 3 times.
        ServiceRegistry sr = (ServiceRegistry) this.pico.getComponentInstance(ServiceRegistry.class);
        assertNotNull(sr);

        ObjectService s = (ObjectService) sr.getService("test");
        assertNotNull(s);

        Invoker invoker = s.getInvoker();
        assertNotNull(invoker);

        Method method = DummyServiceThatCounts.class.getMethod("theMethod", new Class[] {});
        assertNotNull(method);

        invoker.invoke(method, new Object[] {}, null);
        invoker.invoke(method, new Object[] {}, null);
        invoker.invoke(method, new Object[] {}, null);

        // Just once
        assertEquals(1, this.instanceCount);

        assertEquals(3, this.callCount);
    }

    public void testNotCachedServiceObject() throws Exception {
        // Create a child container
        MutablePicoContainer picoChild = new DefaultPicoContainer(this.pico);
        picoChild.registerComponentInstance(this);
        picoChild.registerComponent(new ConstructorInjectionComponentAdapter(DummyServiceThatCounts.class, DummyServiceThatCounts.class));

        // Update pico reference
        this.xfireVisitor.setPicocontainer(picoChild);

        // Execute it 3 times.
        ServiceRegistry sr = (ServiceRegistry) this.pico.getComponentInstance(ServiceRegistry.class);
        assertNotNull(sr);

        ObjectService s = (ObjectService) sr.getService("test");
        assertNotNull(s);

        Invoker invoker = s.getInvoker();
        assertNotNull(invoker);

        Method method = DummyServiceThatCounts.class.getMethod("theMethod", new Class[] {});
        assertNotNull(method);

        invoker.invoke(method, new Object[] {}, null);
        invoker.invoke(method, new Object[] {}, null);
        invoker.invoke(method, new Object[] {}, null);

        // Now its 3 times!
        assertEquals(3, this.instanceCount);

        assertEquals(3, this.callCount);
    }

    public class DummyServiceThatCounts {

        private final XFireServiceRegisterVisitorTest test;

        public DummyServiceThatCounts(XFireServiceRegisterVisitorTest test) {
            this.test = test;
            this.test.instanceCount++;
        }

        public void theMethod() {
            this.test.callCount++;
            return;
        }
    }

}
