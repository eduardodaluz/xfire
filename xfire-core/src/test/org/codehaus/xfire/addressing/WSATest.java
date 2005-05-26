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
        
        EndpointReference200502 epr = new EndpointReference200502(doc.getRootElement());
        assertEquals("http://example.com/fabrikam/acct", epr.getAddress());
        assertEquals(new QName("http://example.com/fabrikam", "Inventory"), epr.getInterfaceName());
        assertNull(epr.getEndpointName());
        assertNull(epr.getServiceName());
    }
}
