package org.codehaus.xfire.plexus.simple;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.xfire.config.ConfigurationException;
import org.codehaus.xfire.handler.SoapHandler;
import org.codehaus.xfire.java.JavaServiceHandler;
import org.codehaus.xfire.plexus.PlexusXFireComponent;
import org.codehaus.xfire.plexus.ServiceInvoker;
import org.codehaus.xfire.plexus.config.Configurator;
import org.codehaus.xfire.plexus.config.PlexusConfigurationAdapter;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.SimpleService;

/**
 * Creates and configures SimpleServices.
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
    
    public Service createService( PlexusConfiguration config ) 
        throws Exception
    {
        SimpleService s = new SimpleService();
        
        configureService((PlexusConfiguration)config, s);

        getServiceRegistry().register(s);
		
        return s;
    }
    
    protected void configureService(PlexusConfiguration config, SimpleService s)
        throws PlexusConfigurationException
    {
        ServiceInvoker invoker = new ServiceInvoker(getServiceLocator());
        JavaServiceHandler handler = new JavaServiceHandler(invoker);
        SoapHandler sHandler = new SoapHandler(handler);
        s.setServiceHandler(sHandler);

        try
        {
            s.configure(new PlexusConfigurationAdapter(config));
        }
        catch (ConfigurationException e)
        {
            throw new PlexusConfigurationException("Couldn't configure service.", e);
        }
    }

    protected ServiceRegistry getServiceRegistry()
    {
        ServiceRegistry registry = null;
        
        try
        {
            registry = (ServiceRegistry) getServiceLocator().lookup( ServiceRegistry.ROLE );
        }
        catch (ComponentLookupException e)
        {
            throw new RuntimeException( "Couldn't find the ServiceRegistry!", e );
        }
        
        return registry;
    }
}
