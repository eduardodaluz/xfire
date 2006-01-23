package org.codehaus.xfire.transport.http;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.test.AbstractServletTest;
import org.codehaus.xfire.util.DOMUtils;
import org.codehaus.xfire.util.STAXUtils;

import com.meterware.httpunit.HttpNotFoundException;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class WSDLDisableTest
    extends AbstractServletTest
{

    public void testWSDLDisabled()
        throws Exception
    {
        try
        {
            WebResponse response = newClient().getResponse(new TestWebRequest(
                    "http://localhost/services/Echo"));

            DOMUtils.writeXml(response.getDOM(), System.out);
        }
        catch (HttpNotFoundException e)
        {
            assertEquals(e.getResponseCode(), 404);
            assertTrue(e.getResponseMessage().indexOf("wsdl") > 0);
            return;
        }
        assertTrue(false);

    }

    public void testWSDLEnabled()
        throws Exception
    {
        try
        {
            WebResponse response = newClient().getResponse(new TestWebRequest(
                    "http://localhost/services/Echo1"));

            DOMUtils.writeXml(response.getDOM(), System.out);
        }
        catch (HttpNotFoundException e)
        {
            assertTrue(false);
        }

    }

    protected String getConfiguration()
    {
        return "/org/codehaus/xfire/transport/http/configurable-web.xml";
    }

    public class TestWebRequest
        extends WebRequest
    {

        protected TestWebRequest(String arg0)
        {
            super(arg0);

        }

        public String getQueryString()
        {

            return "wsdl";
        }

        public String getMethod()
        {

            return "GET";
        }

    }
}
