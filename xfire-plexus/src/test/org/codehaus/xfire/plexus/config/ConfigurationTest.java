package org.codehaus.xfire.plexus.config;

import org.codehaus.xfire.plexus.PlexusXFireTest;
import org.codehaus.xfire.service.Service;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 20, 2004
 */
public class ConfigurationTest 
    extends PlexusXFireTest
{
    public void setUp()
        throws Exception
    {
        System.setProperty("xfire.config", "/org/codehaus/xfire/plexus/config/services.xml");
        super.setUp();
    }
    
    public void testRegister()
        throws Exception
    {
        Service s = getServiceRegistry().getService("Echo");
        
        assertNotNull( s ); 
        
        Service js = getServiceRegistry().getService("Echo2");
        
        assertNotNull( js ); 
        
        Service xs = getServiceRegistry().getService("Echo3");
        
        assertNotNull( xs );
        assertNotNull( xs.getServiceHandler() );
        assertNotNull( xs.getWSDL() );
    }
}
