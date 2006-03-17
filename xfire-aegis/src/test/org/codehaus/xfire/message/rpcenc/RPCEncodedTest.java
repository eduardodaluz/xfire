package org.codehaus.xfire.message.rpcenc;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.BeanInvoker;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.Echo;
import org.codehaus.xfire.test.EchoImpl;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.jdom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 21, 2004
 */
public class RPCEncodedTest
        extends AbstractXFireAegisTest
{
    private Service service;

    public void setUp()
            throws Exception
    {
        super.setUp();

        ((ObjectServiceFactory) getServiceFactory()).setStyle(SoapConstants.STYLE_RPC);
        ((ObjectServiceFactory) getServiceFactory()).setUse(SoapConstants.USE_ENCODED);
        
        service = getServiceFactory().create(Echo.class, "Echo", "urn:Echo", null);
        service.setInvoker(new BeanInvoker(new EchoImpl()));
        
        getServiceRegistry().register(service);
    }

    public void testBeanService()
            throws Exception
    {
        Document response =
                invokeService("Echo", "/org/codehaus/xfire/message/rpcenc/echo11.xml");

        addNamespace("echo", "urn:Echo");
        assertValid("/s:Envelope/s:Body/echo:echoResponse", response);
        assertValid("//echo:echoResponse/echo:out", response);
    }
    
    public void testClient()
        throws Exception
    {
        XFireProxyFactory xpf = new XFireProxyFactory(getXFire());
        Echo echo = (Echo) xpf.create(service, "xfire.local://Echo");
        
        assertEquals("hi", echo.echo("hi"));
    }

    public void testEchoWSDL()
            throws Exception
    {
        Document doc = getWSDLDocument("Echo");

        addNamespace("wsdl", WSDLWriter.WSDL11_NS);
        addNamespace("wsdlsoap", WSDLWriter.WSDL11_SOAP_NS);
        addNamespace("xsd", SoapConstants.XSD);

        assertValid("/wsdl:definitions/wsdl:message[@name='echoRequest']", doc);
        assertValid("/wsdl:definitions/wsdl:message[@name='echoRequest']" +
                    "/wsdl:part[@type='xsd:string'][@name='in0']", doc);
        assertValid("/wsdl:definitions/wsdl:message[@name='echoResponse']", doc);
        assertValid("/wsdl:definitions/wsdl:message[@name='echoResponse']" +
                    "/wsdl:part[@type='xsd:string'][@name='out']", doc);
        assertValid("//wsdl:binding/wsdl:operation[@name='echo']", doc);

        assertValid("//wsdl:binding/wsdl:operation/wsdl:input[@name='echoRequest']" +
                    "/wsdlsoap:body", doc);
        assertValid("//wsdl:binding/wsdl:operation/wsdl:input[@name='echoRequest']" +
                    "/wsdlsoap:body[@encodingStyle='" +
                    Soap11.getInstance().getSoapEncodingStyle() + "']", doc);
        assertValid("//wsdl:binding/wsdl:operation/wsdl:input[@name='echoRequest']" +
                    "/wsdlsoap:body[@use='encoded']", doc);
        assertValid("//wsdl:binding/wsdl:operation/wsdl:input[@name='echoRequest']" +
                    "/wsdlsoap:body[@namespace='" +
                    service.getTargetNamespace() + "']", doc);

        assertValid("//wsdl:binding/wsdl:operation/wsdl:output[@name='echoResponse']" +
                    "/wsdlsoap:body", doc);
        assertValid("//wsdl:binding/wsdl:operation/wsdl:output[@name='echoResponse']" +
                    "/wsdlsoap:body[@encodingStyle='" +
                    Soap11.getInstance().getSoapEncodingStyle() + "']", doc);
        assertValid("//wsdl:binding/wsdl:operation/wsdl:output[@name='echoResponse']" +
                    "/wsdlsoap:body[@use='encoded']", doc);
        assertValid("//wsdl:binding/wsdl:operation/wsdl:output[@name='echoResponse']" +
                    "/wsdlsoap:body[@namespace='" +
                    service.getTargetNamespace() + "']", doc);

        assertValid(
                "/wsdl:definitions/wsdl:service/wsdl:port/wsdlsoap:address[@location='http://localhost/services/Echo']",
                doc);
    }
}
