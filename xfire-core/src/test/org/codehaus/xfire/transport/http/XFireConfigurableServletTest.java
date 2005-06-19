package org.codehaus.xfire.transport.http;

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
    protected String getConfiguration()
    {
        return "/org/codehaus/xfire/transport/http/configurable-web.xml";
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
        
        assertTrue(reg.hasService("Echo1"));
        Service echo1 = reg.getService("Echo1");
        assertTrue(echo1.getSoapVersion() instanceof Soap12);

        assertNotNull(echo1.getInPipeline());
        assertEquals(1, echo1.getInPipeline().size());
        assertNotNull(echo1.getOutPipeline());
        assertEquals(1, echo1.getOutPipeline().size());
    }
}
