package org.codehaus.xfire.plexus;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.codehaus.plexus.embed.Embedder;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;

/**
 * <p>
 * The StandaloneXFire class allows you to embed a Plexus managed version
 * of XFire within your application. Use the XFireFactory to access it.
 * </p>
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PlexusXFireFactory
	extends XFireFactory
{
    private static PlexusXFireFactory standalone;

    protected Embedder embed;

    protected PlexusXFireFactory() 
        throws Exception
    {
        URL resource = getPlexusConfiguration();
        
        embed = new Embedder();

        embed.setConfiguration( resource );

        Properties contextProperties = new Properties();

        embed.setProperties(contextProperties);
        
        embed.start();
    }

    public static XFireFactory createInstance() 
    	throws Exception
    {
        if ( standalone == null )
        {
            synchronized( PlexusXFireFactory.class )
            {
                standalone = new PlexusXFireFactory();
            }
        }
        
        return standalone;
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
            resource = getClass().getResource("/org/codehaus/xfire/plexus/StandaloneXFire.xml");
        }
        
		return resource;
	}

    public XFire getXFire() throws Exception
    {
        return (XFire) embed.lookup( XFire.ROLE );
    }

	protected void finalize() throws Throwable
	{
		embed.stop();
        
        super.finalize();
	}
}
