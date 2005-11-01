package org.codehaus.xfire.transport.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.addressing.AddressingInHandler;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.test.AbstractServletTest;

import com.meterware.httpunit.HttpNotFoundException;


/**
 * XFireServletTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XFireConfigurableServletTest
        extends AbstractServletTest
{
   private Log log = LogFactory.getLog(XFireConfigurableServletTest.class);
    
    protected String getConfiguration()
    {
        return "/org/codehaus/xfire/transport/http/configurable-web.xml";
    }
    public void setUp()
        throws Exception
    {
        // TODO Auto-generated method stub
        super.setUp();
    }
    public void testServlet()
            throws Exception
    {
        // Load up servlet
            
        try
        {
            newClient().getResponse("http://localhost/services/");
        }
        catch(HttpNotFoundException e) {}
        
    	ServiceRegistry reg = getXFire().getServiceRegistry();
        
        assertTrue(reg.hasService("Echo"));
        Service echo = reg.getService("Echo");
        assertNotNull(echo.getServiceInfo().getName().getNamespaceURI());
        assertNotSame("", echo.getServiceInfo().getName().getNamespaceURI());
        
        assertTrue(reg.hasService("Echo1"));
        Service echo1 = reg.getService("Echo1");
        assertTrue(echo1.getSoapVersion() instanceof Soap12);

        assertEquals(2, echo1.getInHandlers().size());
        assertTrue(echo1.getInHandlers().get(1) instanceof AddressingInHandler);
        assertEquals(2, echo1.getOutHandlers().size());
        assertTrue(echo1.getOutHandlers().get(1) instanceof AddressingInHandler);        
    }
}
