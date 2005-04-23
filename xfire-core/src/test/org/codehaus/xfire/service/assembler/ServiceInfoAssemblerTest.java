package org.codehaus.xfire.service.assembler;


import junit.framework.TestCase;
import org.codehaus.xfire.service.ServiceInfo;

public class ServiceInfoAssemblerTest
        extends TestCase
{

    public void testGetServiceInfo()
            throws Exception
    {
        AbstractServiceInfoAssembler assembler = new AbstractServiceInfoAssembler()
        {

            protected void populate(ServiceInfo serviceInfo)
            {
            }
        };
        assertNotNull(assembler.getServiceInfo());
    }
}