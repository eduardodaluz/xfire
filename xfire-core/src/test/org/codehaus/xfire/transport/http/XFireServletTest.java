package org.codehaus.xfire.transport.http;

import org.codehaus.xfire.SOAPConstants;
import org.codehaus.xfire.fault.SOAP12FaultHandler;
import org.codehaus.xfire.service.SimpleService;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * XFireServletTest
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XFireServletTest
    extends AbstractServletTest
{
    public void setUp() throws Exception
    {
        super.setUp();
        
        SimpleService service = new SimpleService();
        service.setName("Echo");
        service.setSoapVersion(SOAPConstants.SOAP12_ENVELOPE_NS);
        service.setWsdlUri(getClass().getResource("/org/codehaus/xfire/echo11.wsdl").toString());
        
        service.setServiceHandler(new MockSessionHandler());
        service.setFaultHandler(new SOAP12FaultHandler());
        
        getServiceRegistry().register(service);
    }
    
    public void testServlet() throws Exception
    {
        WebResponse response = newClient().getResponse( "http://localhost/services/Echo?wsdl" );
        
        System.out.println(response.getText());
        
        WebRequest req = new PostMethodWebRequest( "http://localhost/services/Echo",
                getClass().getResourceAsStream("/org/codehaus/xfire/echo11.xml"),
                "text/xml" );

        response = newClient().getResponse(req);
        
        assertTrue( MockSessionHandler.inSession );
    }
}
