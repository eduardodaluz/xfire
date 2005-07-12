package org.codehaus.xfire.soap.handler;

import org.codehaus.xfire.soap.SoapTransport;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.http.SoapHttpTransport;

public class SoapTransportTest
    extends AbstractXFireTest
{
    public void testHandler() throws Exception
    {
        Transport t = SoapTransport.createSoapTransport(new SoapHttpTransport());
        
        assertEquals(2, t.getInHandlers().size());
        assertEquals(1, t.getOutHandlers().size());
    }
}
