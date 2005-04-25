package org.codehaus.xfire.service.binding;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.test.AbstractXFireTest;

/**
 * @author <a href="mailto:dan@envoisolutiosn.com">Dan Diephouse</a>
 */
public class MessengerTest
    extends AbstractXFireTest
{
    public void setUp() throws Exception
    {
        super.setUp();
    }
    
    public void testInvoke()
        throws Exception
    {
        Service service = getServiceFactory().create(Messenger.class);
        
        assertNotNull(((Service) service).getBindingProvider());
    }
}
