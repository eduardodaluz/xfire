package org.codehaus.xfire.soap.handler;

import org.codehaus.xfire.soap.SoapTransportHelper;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.http.HttpTransport;

public class SoapTransportTest
    extends AbstractXFireTest
{
    public void testHandler() throws Exception
    {
        Transport t = SoapTransportHelper.createSoapTransport(new HttpTransport());
        
        assertEquals(5, t.getInHandlers().size());
        assertEquals(3, t.getOutHandlers().size());
        assertEquals(3, t.getFaultHandlers().size());
    }
}