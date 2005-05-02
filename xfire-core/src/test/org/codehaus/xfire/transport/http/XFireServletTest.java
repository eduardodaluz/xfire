package org.codehaus.xfire.transport.http;

import javax.xml.namespace.QName;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import org.codehaus.xfire.fault.Soap12FaultHandler;
import org.codehaus.xfire.handler.AsyncHandler;
import org.codehaus.xfire.handler.BadHandler;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.binding.SOAPBinding;
import org.codehaus.xfire.service.binding.SOAPBindingFactory;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapHandler;
import org.codehaus.xfire.test.AbstractServletTest;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.yom.Document;

/**
 * XFireServletTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XFireServletTest
        extends AbstractServletTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();

        ServiceEndpoint service = getServiceFactory().create(Echo.class);
        service.setWSDLURL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl").toString());

        service.setRequestPipeline(new HandlerPipeline());
        service.getRequestPipeline().addHandler(new MockSessionHandler());
        getServiceRegistry().register(service);

        // A service which throws a fault
        ServiceInfo faultInfo = new ServiceInfo(new QName("Exception"), getClass());
        SOAPBinding faultBinding = SOAPBindingFactory.createDocumentBinding(new QName("EchoBinding"),
                                                                            Soap12.getInstance());
        ServiceEndpoint faultEndpoint = new ServiceEndpoint(faultInfo, faultBinding);

        faultEndpoint.setServiceHandler(new SoapHandler(new BadHandler()));
        faultEndpoint.setFaultHandler(new Soap12FaultHandler());

        getServiceRegistry().register(faultEndpoint);
        
        // Asynchronous service
        ServiceInfo asyncInfo = new ServiceInfo(new QName("Async"), getClass());
        SOAPBinding asyncBinding = SOAPBindingFactory.createDocumentBinding(new QName("EchoBinding"),
                                                                            Soap12.getInstance());
        ServiceEndpoint asyncEndpoint = new ServiceEndpoint(asyncInfo, asyncBinding);
        asyncEndpoint.setWSDLURL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl").toString());

        asyncEndpoint.setServiceHandler(new SoapHandler(new AsyncHandler()));
        asyncEndpoint.setFaultHandler(new Soap12FaultHandler());

        getServiceRegistry().register(asyncEndpoint);
    }

    public void testServlet()
            throws Exception
    {
        WebResponse response = newClient().getResponse("http://localhost/services/Echo?wsdl");

        WebRequest req = new PostMethodWebRequest("http://localhost/services/Echo",
                                                  getClass().getResourceAsStream("/org/codehaus/xfire/echo11.xml"),
                                                  "text/xml");

        response = newClient().getResponse(req);

        Document doc = readDocument(response.getText());
        addNamespace("m", "urn:Echo");
        assertValid("//m:echo", doc);

        assertTrue(MockSessionHandler.inSession);
    }

    public void testFaultCode()
            throws Exception
    {
        WebRequest req = new PostMethodWebRequest("http://localhost/services/Exception",
                                                  getClass().getResourceAsStream("/org/codehaus/xfire/echo11.xml"),
                                                  "text/xml");

        Transport transport = getXFire().getTransportManager().getTransport(SoapHttpTransport.NAME);
        assertNotNull(transport.getFaultPipeline());

        expectErrorCode(req, 500, "Response code 500 required for faults.");
    }

    public void testServiceWsdlNotFound()
            throws Exception
    {
        WebRequest req = new GetMethodWebRequest("http://localhost/services/NoSuchService?wsdl");

        expectErrorCode(req, 404, "Response code 404 required for invalid WSDL url.");
    }

    public void testAsync()
            throws Exception
    {
        WebRequest req = new PostMethodWebRequest("http://localhost/services/Async",
                                                  getClass().getResourceAsStream("/org/codehaus/xfire/echo11.xml"),
                                                  "text/xml");

        WebResponse response = newClient().getResponse(req);
        assertTrue(response.getText().length() == 0);
    }
}
