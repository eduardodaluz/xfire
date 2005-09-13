package org.codehaus.xfire.aegis.type.basic.recursive;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;

public class RecursiveTest
    extends AbstractXFireAegisTest
{
    public void setUp() throws Exception
    {
        super.setUp();
        
        getServiceRegistry().register(getServiceFactory().create(RecursiveService.class));
    }
    
    public void testService() throws Exception
    {
        invokeService("RecursiveService", "/org/codehaus/xfire/aegis/type/basic/recursive/GetRecursiveBean.xml");
    }
    
    public void testWSDL() throws Exception
    {
        getWSDLDocument("RecursiveService");
    }
    
    public static class RecursiveService
    {
        public Bean1 getRecursiveBean()
        {
            Bean1 bean = new Bean1();
            bean.setBean2(new Bean2());
            return bean;
        }
    }
}
