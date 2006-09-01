package org.codehaus.xfire.wsdl11;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.test.Echo;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DocLitWSDLTest
    extends AbstractXFireAegisTest
{
    public void testWSDL()
        throws Exception
    {
        Service service = getServiceFactory().create(Echo.class, 
                                                     new QName("urn:xfire:wsdl", "EchoService"),
                                                     getClass().getResource("echoDocLit.wsdl"),
                                                     null);
        
        
    }
}
