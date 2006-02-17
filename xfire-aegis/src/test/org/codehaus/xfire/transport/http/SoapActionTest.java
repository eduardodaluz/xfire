package org.codehaus.xfire.transport.http;

import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractServletTest;

import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * XFireServletTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SoapActionTest
        extends AbstractServletTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();

        ObjectServiceFactory osf = new ObjectServiceFactory(getTransportManager()) {
            protected String getAction(OperationInfo op)
            {
                return op.getName();
            }
        };
        osf.setStyle(SoapConstants.STYLE_DOCUMENT);
        
        Service service = osf.create(SoapActionService.class);
        getServiceRegistry().register(service);
    }

    public void testSoapAction()
            throws Exception
    {
        HttpUnitOptions.setLoggingHttpHeaders(true);

        WebRequest req = new PostMethodWebRequest("http://localhost/services/SoapActionService",
                                                  getResourceAsStream("empty.xml"),
                                                  "text/xml");
        req.setHeaderField("SOAPAction", "one");

        WebResponse response = newClient().getResponse(req);
        assertTrue(response.getText().indexOf("oneout") != -1);
       
        req = new PostMethodWebRequest("http://localhost/services/SoapActionService",
                                       getResourceAsStream("empty.xml"),
                                       "text/xml");
        req.setHeaderField("SOAPAction", "two");
        
        response = newClient().getResponse(req);
        assertTrue(response.getText().indexOf("twoout") != -1);

    }
}
