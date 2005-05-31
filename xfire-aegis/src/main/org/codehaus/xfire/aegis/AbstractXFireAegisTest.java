package org.codehaus.xfire.aegis;

import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.test.AbstractXFireTest;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 * @since Oct 31, 2004
 */
public class AbstractXFireAegisTest
    extends AbstractXFireTest
{
    protected void setUp()
        throws Exception
    {
        super.setUp();

        setServiceFactory(new ObjectServiceFactory(getXFire().getTransportManager(), 
                                                   new AegisBindingProvider()));
    }
}
