package org.codehaus.xfire.addressing;

import javax.xml.namespace.QName;

import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.yom.Document;
import org.codehaus.yom.stax.StaxBuilder;

public class WSATest
    extends AbstractXFireTest
{
    public void testEPRs() throws Exception
    {
        StaxBuilder builder = new StaxBuilder();
        Document doc =builder.build(getResourceAsStream("/org/codehaus/xfire/addressing/EPR1.xml"));
        
        AddressingHeadersFactory200502 factory = new AddressingHeadersFactory200502();
        EndpointReference epr = factory.createEPR(doc.getRootElement());
        assertEquals("http://example.com/fabrikam/acct", epr.getAddress());
        assertEquals(new QName("http://example.com/fabrikam", "Inventory"), epr.getInterfaceName());
        assertNull(epr.getEndpointName());
        assertNull(epr.getServiceName());
    }
    
    public void testHeaders() throws Exception
    {
        StaxBuilder builder = new StaxBuilder();
        Document doc =builder.build(getResourceAsStream("/org/codehaus/xfire/addressing/Headers1.xml"));
        
        AddressingHeadersFactory200502 factory = new AddressingHeadersFactory200502();
        
        assertTrue(factory.hasHeaders(doc.getRootElement()));
        
        AddressingHeaders headers = factory.createHeaders(doc.getRootElement());
        assertEquals("http://example.com/6B29FC40-CA47-1067-B31D-00DD010662DA", 
                     headers.getMessageID());
        
        assertNotNull(headers.getReplyTo());
        assertEquals("http://example.com/business/client1",
                     headers.getReplyTo().getAddress());
        assertEquals("http://example.com/fabrikam/Purchasing",
                     headers.getTo());
        assertEquals("http://example.com/fabrikam/SubmitPO", headers.getAction());
    }
}
