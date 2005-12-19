package org.codehaus.xfire.message.document;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 21, 2004
 */
public class DocumentServiceTest
        extends AbstractXFireAegisTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();

        ((ObjectServiceFactory) getServiceFactory()).setStyle(SoapConstants.STYLE_DOCUMENT);
        Service service = getServiceFactory().create(DocumentService.class, "Doc", "urn:Doc", null);

        getServiceRegistry().register(service);
    }

    public void testNoParams()
            throws Exception
    {
        Document response =
                invokeService("Doc", "/org/codehaus/xfire/message/document/document11-1.xml");

        addNamespace("d", "urn:Doc");
        assertValid("//d:getString1out", response);
        assertValid("//d:getString1out[text()=\"string\"]", response);
    }

    public void testOneParam()
            throws Exception
    {
        Document response =
                invokeService("Doc", "/org/codehaus/xfire/message/document/document11-2.xml");

        addNamespace("d", "urn:Doc");
        assertValid("//d:getString2out", response);
        assertValid("//d:getString2out[text()=\"bleh\"]", response);
    }

    public void testTwoParams()
            throws Exception
    {
        Document response =
                invokeService("Doc", "/org/codehaus/xfire/message/document/document11-3.xml");

        addNamespace("d", "urn:Doc");
        assertValid("//d:getString3out", response);
        assertValid("//d:getString3out[text()=\"blehbleh2\"]", response);
    }     
}
