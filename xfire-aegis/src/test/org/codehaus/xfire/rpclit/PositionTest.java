package org.codehaus.xfire.rpclit;

import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.test.AbstractXFireTest;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PositionTest
    extends AbstractXFireTest
{
    public void testVisitor()
        throws Exception
    {
        Client client = new Client(getClass().getResource("positionService.wsdl"));
        
        assertNotNull(client.getService().getServiceInfo().getOperation("getCurrentPosition"));
    }
}
