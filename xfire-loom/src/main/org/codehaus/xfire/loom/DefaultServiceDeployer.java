package org.codehaus.xfire.loom;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;

/**
 * Default implementation of ServiceDeployer
 *
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class DefaultServiceDeployer extends AbstractLogEnabled implements ServiceDeployer, Serviceable, Configurable
{
    private final Map m_services = Collections.synchronizedMap( new HashMap() );

    private Map m_factories;
    private ServiceRegistry m_registry;

    private Map m_configurations;

    public void configure( final Configuration configuration ) throws ConfigurationException
    {
        final Configuration[] kids = configuration.getChildren( "service" );

        m_configurations = new HashMap( kids.length );

        for( int i = 0; i < kids.length; i++ )
        {
            m_configurations.put( kids[i].getAttribute( "key" ), kids[i] );
        }
    }

    public void service( final ServiceManager manager ) throws ServiceException
    {
        final ServiceFactory[] factories = (ServiceFactory[])manager.lookup( ServiceFactory.class.getName() + "[]" );

        m_factories = new HashMap( factories.length );

        for( int i = 0; i < factories.length; i++ )
        {
            m_factories.put( factories[i].getType(), factories[i] );
        }

        m_registry = (ServiceRegistry)manager.lookup( ServiceRegistry.ROLE );
    }

    public void deploy( final String key, final Object object ) throws Exception
    {
        if( m_services.containsKey( key ) )
        {
            throw new IllegalStateException( "Service with key '" + key + "' already deployed" );
        }

        Configuration configuration = (Configuration)m_configurations.get( key );

        if( null == configuration )
        {
            if( getLogger().isInfoEnabled() )
                getLogger().info( "No configuration found for '" + key + "', generating template" );

            configuration = createTemplateConfiguration( key );
        }

        final String type = configuration.getAttribute( "type" );
        final ServiceFactory factory = (ServiceFactory)m_factories.get( type );

        if( null == factory )
        {
            final String msg = "Service '" + key + "' is to be created via '" + type + "' factory, but none exists";
            throw new IllegalStateException( msg );
        }
        else
        {
            final Service service = factory.createService( object, configuration );

            if( getLogger().isDebugEnabled() )
                getLogger().debug( "Created '" + service.getName() + "' from key '" + key + "'" );

            m_registry.register( service );

            m_services.put( key, service.getName() );
        }
    }

    private Configuration createTemplateConfiguration( final String key )
    {
        final DefaultConfiguration configuration = new DefaultConfiguration( key );
        final DefaultConfiguration name = new DefaultConfiguration( "name" );

        name.setValue( key );
        name.makeReadOnly();

        configuration.setAttribute( "type", "simple " );
        configuration.addChild( name );

        configuration.makeReadOnly();

        return configuration;
    }

    public void undeploy( final String key )
    {
        if( m_services.containsKey( key ) )
        {
            m_registry.unregister( (String)m_services.remove( key ) );
        }
        else if( getLogger().isWarnEnabled() )
        {
            getLogger().warn( "Attempted to undeploy unknown key: " + key );
        }
    }
}