package org.codehaus.xfire.loom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.ApplicationEvent;
import org.apache.avalon.phoenix.ApplicationListener;
import org.apache.avalon.phoenix.BlockEvent;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;

/**
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class XFireBlockDeployerApplicationListener extends AbstractLogEnabled
    implements ApplicationListener, Configurable
{
    private ServiceDeployer m_deployer;
    private Map m_services;

    public void configure( final Configuration configuration ) throws ConfigurationException
    {
        final Configuration[] kids = configuration.getChildren( "service" );

        m_services = new HashMap( kids.length );

        for( int i = 0; i < kids.length; i++ )
        {
            m_services.put( kids[i].getValue(), null );
        }
    }

    public void applicationFailure( final Exception e )
    {
    }

    public void applicationStarted()
    {
        if( null == m_deployer )
        {
            throw new IllegalStateException( "Must provide a " + ServiceDeployer.class.getName() );
        }

        final Iterator i = m_services.entrySet().iterator();

        while( i.hasNext() )
        {
            final Map.Entry entry = (Map.Entry)i.next();

            if( null == entry.getValue() )
            {
                throw new IllegalStateException( "Unable to deploy configured service: " + entry.getKey() );
            }

            if( getLogger().isDebugEnabled() ) getLogger().debug( "Deploying '" + entry.getKey() + "'" );

            try
            {
                m_deployer.deploy( (String)entry.getKey(), entry.getValue() );
            }
            catch( Exception e )
            {
                throw new RuntimeException( "Unable to deploy '" + entry.getKey() + "'", e );
            }
        }
    }

    public void applicationStarting( final ApplicationEvent event ) throws Exception
    {
    }

    public void applicationStopped()
    {
    }

    public void applicationStopping()
    {
        final Iterator i = m_services.keySet().iterator();

        while( i.hasNext() )
        {
            final String service = (String)i.next();

            if( getLogger().isDebugEnabled() ) getLogger().debug( "Undeploying '" + service + "'" );

            m_deployer.undeploy( service );
        }
    }

    public void blockAdded( final BlockEvent event )
    {
        if( isServiceDeployer( event.getBlockInfo() ) )
        {
            if( null == m_deployer )
            {
                if( getLogger().isDebugEnabled() )
                    getLogger().debug( "Using '" + event.getName() + "' as ServiceDeployer" );

                m_deployer = (ServiceDeployer)event.getObject();
            }
            else
            {
                throw new RuntimeException( "Duplicate ServiceDeployer found: " + event.getName() );
            }
        }
        else if( m_services.containsKey( event.getName() ) )
        {
            if( getLogger().isDebugEnabled() )
                getLogger().debug( "Marking '" + event.getName() + "' for XFire deploment" );

            m_services.put( event.getName(), event.getObject() );
        }
    }

    private boolean isServiceDeployer( final BlockInfo blockInfo )
    {
        final ServiceDescriptor[] services = blockInfo.getServices();

        for( int i = 0; i < services.length; i++ )
        {
            if( ServiceDeployer.class.getName().equals( services[i].getName() ) )
            {
                return true;
            }
        }

        return false;
    }

    public void blockRemoved( final BlockEvent event )
    {
    }
}