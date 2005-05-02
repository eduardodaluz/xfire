package org.codehaus.xfire.service.binding;

import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.test.AbstractXFireTest;

/**
 * @author <a href="mailto:dan@envoisolutiosn.com">Dan Diephouse</a>
 */
public class MessengerTest
        extends AbstractXFireTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();
    }

    public void testInvoke()
            throws Exception
    {
        ServiceEndpoint service = getServiceFactory().create(Messenger.class);

        assertNotNull(service.getBindingProvider());
    }
}
