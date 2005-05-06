package org.codehaus.xfire;

import java.io.ByteArrayOutputStream;

import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.wsdl.ResourceWSDL;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.yom.Document;

/**
 * XFireTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XFireTest
        extends AbstractXFireTest
{

    public void setUp()
            throws Exception
    {
        super.setUp();

        ServiceEndpoint service = getServiceFactory().create(Echo.class,
                                                             Soap12.getInstance(),
                                                             SoapConstants.STYLE_MESSAGE,
                                                             SoapConstants.USE_LITERAL);
        WSDLWriter writer = new ResourceWSDL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl"));
        service.setWSDLWriter(writer);

        getServiceRegistry().register(service);
    }

    public void testInvoke()
            throws Exception
    {
        Document response = invokeService("Echo", "/org/codehaus/xfire/echo11.xml");
        printNode(response);
        addNamespace("m", "urn:Echo");
        assertValid("//m:echo", response);
    }

    public void testWSDL()
            throws Exception
    {
        ServiceEndpoint service = (ServiceEndpoint) getServiceRegistry().getServiceEndpoint("Echo");

        WSDLWriter wsdl = service.getWSDLWriter();

        assertNotNull(wsdl);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        getXFire().generateWSDL("Echo", out);
    }
}
