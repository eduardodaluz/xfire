package org.codehaus.xfire.plexus;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.TransportManager;

/**
 * <p>
 * The StandaloneXFire class allows you to embed XFire any which way
 * within your apps.  Grab an instance of the <code>StandaloneXFire</code>
 * instance via <code>getInstance()</code> then access such components
 * as the ServiceRegistry or TransportService.
 * </p>
 * <p>
 * This class assumes one XFire instance per JVM. To create many XFire instances
 * you must use Plexus directly.
 * </p>
 * <p>
 * To use a non-standard plexus configuration for XFire, set the
 * "xfire.plexusConfig" system property to the location of the configuration
 * file.  This can be in the classpath or in the filesystem.
 * </p>
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class StandaloneXFire
{
    private static StandaloneXFire standalone;
    
    protected Embedder embed;

    private StandaloneXFire() 
        throws Exception
    {
        URL resource = getPlexusConfiguration();
        
        embed = new Embedder();

        embed.setConfiguration( resource );

        Properties contextProperties = new Properties();

        embed.setProperties(contextProperties);
        
        embed.start();
    }

    /**
	 * @return
	 */
	private URL getPlexusConfiguration()
	{
		URL resource = null;
        
        String configFileName = System.getProperty("xfire.plexusConfig");
        
        if ( configFileName != null ) 
        {
            File file = new File(configFileName);
            if ( file.exists() )
            {
                try
    			{
    				resource = file.toURL();
    			}
    			catch (MalformedURLException e)
    			{
                    throw new RuntimeException("Couldn't get plexus configuration.", e);
    			}
            }
            else
            {
                resource = getClass().getResource(configFileName);
            }
        }

        if ( resource == null )
        {
            resource = getClass().getResource("/org/codehaus/xfire/StandaloneXFire.xml");
        }
        
		return resource;
	}

	public static StandaloneXFire getInstance() 
        throws Exception
    {
        if (standalone == null)
		{
			synchronized (StandaloneXFire.class)
			{
				standalone = new StandaloneXFire();
			}
		}
		return standalone;
    }
    
    public ServiceRegistry getServiceRegistry() throws Exception
    {
        return (ServiceRegistry) embed.lookup( ServiceRegistry.ROLE );
    }
    
    public XFire getXFire() throws Exception
    {
        return (XFire) embed.lookup( XFire.ROLE );
    }
    
    public TransportManager getTransportService() throws Exception
    {
        return (TransportManager) embed.lookup( TransportManager.ROLE );
    }
    
	protected void finalize() throws Throwable
	{
		embed.stop();
        
        super.finalize();
	}
}
