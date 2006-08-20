package org.codehaus.xfire.aegis.inheritance;

import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.inheritance.ws1.WS1;
import org.codehaus.xfire.aegis.inheritance.ws1.WS1ExtendedException;
import org.codehaus.xfire.aegis.inheritance.ws1.impl.WS1Impl;
import org.codehaus.xfire.aegis.type.basic.SimpleBean;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.invoker.BeanInvoker;

public class ExceptionInheritanceTest extends AbstractXFireAegisTest
{
    private Service service;

    public void setUp() throws Exception 
    {
        super.setUp();

        HashMap props = new HashMap();
        props.put(AegisBindingProvider.WRITE_XSI_TYPE_KEY, Boolean.TRUE);
        ArrayList l = new ArrayList();
        l.add(SimpleBean.class.getName());
        l.add(WS1ExtendedException.class.getName());

        props.put(AegisBindingProvider.OVERRIDE_TYPES_KEY, l);
        
        service = getServiceFactory().create(WS1.class, props);
        service.setInvoker(new BeanInvoker(new WS1Impl()));
        getServiceRegistry().register(service);
    }
    
    public void testClient() throws Exception 
    {
        WS1 client = (WS1) new XFireProxyFactory(getXFire()).create(service, "xfire.local://WS1");
        
        try 
        {
            client.throwException(true);
        }
        catch (WS1ExtendedException ex)
        {
            Object sb = ex.getSimpleBean();
            assertTrue(sb instanceof SimpleBean);
        }
    }
}
