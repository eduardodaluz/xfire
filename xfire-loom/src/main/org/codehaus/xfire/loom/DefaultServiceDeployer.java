package org.codehaus.xfire.loom;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.object.Invoker;
import org.codehaus.xfire.service.object.ObjectServiceBuilder;
import org.codehaus.xfire.service.object.ServiceBuilder;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.type.TypeMappingRegistry;

/**
 * Default implementation of ServiceDeployer
 *
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class DefaultServiceDeployer extends AbstractLogEnabled implements ServiceDeployer, Serviceable, Configurable
{
    private final Map m_services = Collections.synchronizedMap( new HashMap() );

    private ServiceRegistry m_serviceRegistry;
    private TypeMappingRegistry m_typeMappingRegistry;
    private TransportManager m_transportManager;

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
        m_serviceRegistry = (ServiceRegistry)manager.lookup( ServiceRegistry.ROLE );
        m_typeMappingRegistry = (TypeMappingRegistry)manager.lookup( TypeMappingRegistry.ROLE );
        m_transportManager = (TransportManager)manager.lookup( TransportManager.ROLE );
    }

    public void deploy( final String key, final Object object ) throws Exception
    {
        if( m_services.containsKey( key ) )
        {
            throw new IllegalStateException( "Service with key '" + key + "' already deployed" );
        }

        final Configuration configuration = (Configuration)m_configurations.get( key );
        final Service service;

        if( null == configuration )
        {
            if( getLogger().isInfoEnabled() )
                getLogger().info( "No configuration found for '" + key + "', using defaults" );

            service = createDefaultBuilder( object ).create( object.getClass() );
        }
        else
        {
            service = createServiceFromConfiguration( configuration, object );

            if( getLogger().isDebugEnabled() )
                getLogger().debug( "Created '" + service.getName() + "' from key '" + key + "'" );
        }

        registerService( key, service );
    }

    private Service createServiceFromConfiguration( final Configuration configuration,
                                                    final Object object ) throws ConfigurationException
    {
        //TODO support building given a WSDL url
        final ServiceBuilder builder = createBuilder( configuration.getChild( "builder" ), object );
        final Service service = builder.create( loadClass( configuration.getChild( "serviceClass" ) ),
                                                configuration.getChild( "name" ).getValue(),
                                                configuration.getChild( "namespace" ).getValue( "" ),
                                                getSoapVersion( configuration.getChild( "soapVersion" ) ),
                                                configuration.getChild( "style" ).getValue( "wrapped" ),
                                                configuration.getChild( "use" ).getValue( "literal" ),
                                                configuration.getChild( "encodingStyleURI" ).getValue( null ) );

        final Configuration[] properties = configuration.getChildren( "property" );

        for( int i = 0; i < properties.length; i++ )
        {
            service.setProperty( properties[i].getAttribute( "name" ), properties[i].getAttribute( "value" ) );
        }

        return service;
    }

    private ServiceBuilder createBuilder( final Configuration configuration, final Object object )
        throws ConfigurationException
    {
        final String builderClassName = configuration.getValue( null );

        if( null == builderClassName )
        {
            return createDefaultBuilder( object );
        }
        else
        {
            final Class clazz = loadClass( configuration );

            if( ServiceBuilder.class.isAssignableFrom( clazz ) )
            {
                try
                {
                    final Constructor cxtor = clazz.getConstructor( new Class[]{TransportManager.class,
                                                                                TypeMappingRegistry.class,
                                                                                Invoker.class} );

                    return (ServiceBuilder)cxtor.newInstance( new Object[]{m_transportManager,
                                                                           m_typeMappingRegistry,
                                                                           new ServiceInvoker( object )} );
                }
                catch( Exception e )
                {
                    final String msg = "Unable to instantiate instance of " + builderClassName
                        + " at " + configuration.getLocation();
                    throw new ConfigurationException( msg, e );
                }
            }
            else
            {
                final String msg = configuration.getValue()
                    + " is not a ServiceBuilder at " + configuration.getLocation();
                throw new ConfigurationException( msg );
            }
        }
    }

    private SoapVersion getSoapVersion( final Configuration configuration ) throws ConfigurationException
    {
        final String value = configuration.getValue( "1.1" );

        if( value.equals( "1.1" ) )
        {
            return Soap11.getInstance();
        }
        else if( value.equals( "1.2" ) )
        {
            return Soap12.getInstance();
        }
        else
        {
            final String msg = "Invalid soap version at " + configuration.getLocation() + ". Must be 1.1 or 1.2.";
            throw new ConfigurationException( msg );
        }
    }

    private Class loadClass( final Configuration configuration )
        throws ConfigurationException
    {
        try
        {
            return Thread.currentThread().getContextClassLoader().loadClass( configuration.getValue() );
        }
        catch( ClassNotFoundException e )
        {
            final String msg = "Unable to load " + configuration.getValue() + " at " + configuration.getLocation();
            throw new ConfigurationException( msg, e );
        }
    }

    private ServiceBuilder createDefaultBuilder( final Object object )
    {
        return new ObjectServiceBuilder( m_transportManager,
                                         m_typeMappingRegistry,
                                         new ServiceInvoker( object ) );
    }

    private void registerService( final String key, final Service service )
    {
        m_serviceRegistry.register( service );

        m_services.put( key, service.getName() );
    }

    public void undeploy( final String key )
    {
        if( m_services.containsKey( key ) )
        {
            m_serviceRegistry.unregister( (String)m_services.remove( key ) );
        }
        else if( getLogger().isWarnEnabled() )
        {
            getLogger().warn( "Attempted to undeploy unknown key: " + key );
        }
    }
}