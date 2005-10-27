package org.codehaus.xfire.message.document;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.services.AddNumbers;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 21, 2004
 */
public class PrimitiveServiceTest
        extends AbstractXFireAegisTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();
        
        ((ObjectServiceFactory) getServiceFactory()).setStyle(SoapConstants.STYLE_DOCUMENT);
        
        getServiceRegistry().register(getServiceFactory().create(AddNumbers.class));
    }

    public void testService()
            throws Exception
    {
        Document response =
                invokeService("AddNumbers",
                              "/org/codehaus/xfire/message/document/add.xml");

        addNamespace("a", "http://services.xfire.codehaus.org");
        assertValid("//a:addout[text()='2']", response);
    }
}
