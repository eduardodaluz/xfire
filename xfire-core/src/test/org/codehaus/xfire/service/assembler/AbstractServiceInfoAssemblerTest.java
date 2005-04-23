package org.codehaus.xfire.service.assembler;


import junit.framework.TestCase;
import org.codehaus.xfire.service.ServiceInfo;

public class AbstractServiceInfoAssemblerTest
        extends TestCase
{

    public void testGetServiceInfo()
            throws Exception
    {
        AbstractServiceInfoAssembler assembler = new AbstractServiceInfoAssembler()
        {
            protected void populate(ServiceInfo serviceInfo)
            {
                assertNotNull(serviceInfo);
            }
        };
        assembler.getServiceInfo();
    }
}