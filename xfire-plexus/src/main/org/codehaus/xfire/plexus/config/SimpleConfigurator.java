package org.codehaus.xfire.plexus.config;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.xfire.plexus.PlexusService;
import org.codehaus.xfire.plexus.PlexusXFireComponent;
import org.codehaus.xfire.service.Service;

/**
 * TODO document SimpleConfigurator
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 20, 2004
 */
public class SimpleConfigurator 
    extends PlexusXFireComponent
    implements Configurator
{
    final public static String SERVICE_TYPE = "simple";
    
    /**
     * @see org.codehaus.xfire.plexus.config.Configurator#getServiceType()
     */
    public String getServiceType()
    {
        return SERVICE_TYPE;
    }
    
    /**
     * @throws Exception
     * @see org.codehaus.xfire.plexus.config.Configurator#createService(org.codehaus.plexus.configuration.PlexusConfiguration)
     */
    public Service createService( PlexusConfiguration config ) 
        throws Exception
    {
        PlexusService s = new PlexusService();
        s.service( getServiceLocator() );
        s.configure( config );
        s.initialize();
        
        return s;
    }
}
