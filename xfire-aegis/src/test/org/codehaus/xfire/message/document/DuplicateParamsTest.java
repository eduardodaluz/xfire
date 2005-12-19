package org.codehaus.xfire.message.document;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 21, 2004
 */
public class DuplicateParamsTest
        extends AbstractXFireAegisTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();

        ((ObjectServiceFactory) getServiceFactory()).setStyle(SoapConstants.STYLE_DOCUMENT);
        Service service = getServiceFactory().create(DupParamsService.class);

        getServiceRegistry().register(service);
    }

    public void testWSDL()
            throws Exception
    {
        try
        {
            Document wsdl = getWSDLDocument("DupParamsService");
            fail("Shouldn't allow duplicate schema elements with same qname.");
        }
        catch (XFireRuntimeException e)
        {
        }
    }

    public static class DupParamsService
    {
        public void doSomething(int foo) {}
        public void doSomething(String foo) {}
    }
}
