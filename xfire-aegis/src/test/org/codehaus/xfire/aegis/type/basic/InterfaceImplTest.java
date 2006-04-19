package org.codehaus.xfire.aegis.type.basic;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.invoker.BeanInvoker;

public class InterfaceImplTest
    extends AbstractXFireAegisTest
{
    public void testInterface() throws Exception
    {
        Service service = getServiceFactory().create(IMyService.class);
        service.setInvoker(new BeanInvoker(new MyService()));
        getServiceRegistry().register(service);
        service.setProperty(IMyObject.class.getName() + ".implementation", MyObject.class.getName());
        
        IMyService client = (IMyService) 
            new XFireProxyFactory(getXFire()).create(service, "xfire.local://IMyService");
        MyObject o = new MyObject();
        o.setName("foo");
        client.receive(o);
    }


    public static interface IMyService
    {
        public void receive(IMyObject o);
    }
    
    public static class MyService implements IMyService
    {
        public void receive(IMyObject o)
        {
            assertTrue(o instanceof MyObject);
        }
    }
    
    public interface IMyObject
    {
        public String getName();
    }

    public static class MyObject implements IMyObject
    {
        private String name;

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }        
    }
    
}
