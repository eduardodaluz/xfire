package org.codehaus.xfire.message.rpcenc;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.services.Echo;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.yom.Document;

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

        service = getServiceFactory().create(Echo.class,
                                             "Echo",
                                             "urn:Echo",
                                             Soap11.getInstance(),
                                             SoapConstants.STYLE_RPC,
                                             SoapConstants.USE_ENCODED, null);

        getServiceRegistry().register(service);
    }

    public void testBeanService()
            throws Exception
    {
        TypeMapping tm = AegisBindingProvider.getTypeMapping(service);
        Type type = tm.getType(String.class);

        Document response =
                invokeService("Echo", "/org/codehaus/xfire/message/rpcenc/echo11.xml");

        addNamespace("echo", "urn:Echo");
        assertValid("/s:Envelope/s:Body/echo:echoResponse", response);
        assertValid("//echo:echoResponse/echo:out", response);
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
                    "/wsdl:part[@element='xsd:string'][@name='in0']", doc);
        assertValid("/wsdl:definitions/wsdl:message[@name='echoResponse']", doc);
        assertValid("/wsdl:definitions/wsdl:message[@name='echoResponse']" +
                    "/wsdl:part[@element='xsd:string'][@name='out']", doc);
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
                    service.getDefaultNamespace() + "']", doc);

        assertValid("//wsdl:binding/wsdl:operation/wsdl:output[@name='echoResponse']" +
                    "/wsdlsoap:body", doc);
        assertValid("//wsdl:binding/wsdl:operation/wsdl:output[@name='echoResponse']" +
                    "/wsdlsoap:body[@encodingStyle='" +
                    Soap11.getInstance().getSoapEncodingStyle() + "']", doc);
        assertValid("//wsdl:binding/wsdl:operation/wsdl:output[@name='echoResponse']" +
                    "/wsdlsoap:body[@use='encoded']", doc);
        assertValid("//wsdl:binding/wsdl:operation/wsdl:output[@name='echoResponse']" +
                    "/wsdlsoap:body[@namespace='" +
                    service.getDefaultNamespace() + "']", doc);

        assertValid(
                "/wsdl:definitions/wsdl:service/wsdl:port/wsdlsoap:address[@location='http://localhost/services/Echo']",
                doc);
    }
}
