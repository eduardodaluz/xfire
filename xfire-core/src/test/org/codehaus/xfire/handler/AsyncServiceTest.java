package org.codehaus.xfire.handler;

import org.codehaus.xfire.fault.Soap12FaultHandler;
import org.codehaus.xfire.service.DefaultService;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapHandler;
import org.codehaus.xfire.test.AbstractXFireTest;

/**
 * @author <a href="mailto:dan@envoisolutiosn.com">Dan Diephouse</a>
 */
public class AsyncServiceTest
    extends AbstractXFireTest
{
    public void setUp() throws Exception
    {
        super.setUp();
        
        DefaultService asyncService = new DefaultService();
        asyncService.setName("Async");
        asyncService.setSoapVersion(Soap12.getInstance());
        asyncService.setWSDLURL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl").toString());
        
        asyncService.setServiceHandler(new SoapHandler(new AsyncHandler()));
        asyncService.setFaultHandler(new Soap12FaultHandler());

        getServiceRegistry().register(asyncService);
    }
    
    public void testInvoke()
        throws Exception
    {
        assertNull(invokeService("Async", "/org/codehaus/xfire/echo11.xml"));
    }
}
