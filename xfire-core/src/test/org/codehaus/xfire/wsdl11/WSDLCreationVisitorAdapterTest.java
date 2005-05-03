package org.codehaus.xfire.wsdl11;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.test.ServiceEndpoints;

public class WSDLCreationVisitorAdapterTest
        extends TestCase
{
    private WSDLCreationVisitorAdapter wsdlCreationVisitorAdapter;

    protected void setUp()
            throws Exception
    {
        ServiceEndpoint endpoint = ServiceEndpoints.getEchoService();
        wsdlCreationVisitorAdapter = new WSDLCreationVisitorAdapter(endpoint);
    }


    public void testWrite()
            throws Exception
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        assertEquals(os.toByteArray().length, 0);
        wsdlCreationVisitorAdapter.write(os);
        assertTrue(os.toByteArray().length > 0);
    }
}