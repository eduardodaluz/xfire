package org.codehaus.xfire.wsdl;

import junit.framework.TestCase;
import org.codehaus.xfire.service.MessageService;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 20, 2004
 */
public class WSDLTest
    extends TestCase
{
    public void testResource()
        throws Exception
    {
        MessageService s = new MessageService();
        s.setWSDLURL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl").toString() );

        WSDLWriter wsdl = s.getWSDLWriter();
        wsdl.write( System.out );
    }
}
