package org.codehaus.xfire.plexus.java;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.xfire.plexus.PlexusXFireComponent;
import org.codehaus.xfire.plexus.config.Configurator;
import org.codehaus.xfire.service.Service;

/**
 * Configures java services for plexus.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 20, 2004
 */
public class JavaConfigurator
    extends PlexusXFireComponent
    implements Configurator
{
    final public static String SERVICE_TYPE = "java";
    
    /**
     * @see org.codehaus.xfire.plexus.config.Configurator#getServiceType()
     */
    public String getServiceType()
    {
        return SERVICE_TYPE;
    }

    /**
     * @see org.codehaus.xfire.plexus.config.Configurator#createService(org.codehaus.plexus.configuration.PlexusConfiguration)
     */
    public Service createService( PlexusConfiguration config ) throws Exception
    {
        PlexusJavaService s = new PlexusJavaService();
        s.service(getServiceLocator());
        s.configure(config);
        s.initialize();
        
        return s;
    }
}
