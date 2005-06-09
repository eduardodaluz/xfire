package org.codehaus.xfire.plexus;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.XFireRuntimeException;

/**
 * <p>
 * The StandaloneXFire class allows you to embed a Plexus managed version
 * of XFire within your application. Use the XFireFactory to access it.
 * </p>
 * <p>
 * If you are not using the StandaloneXFireServlet or PlexusXFireServlet,
 * you must register this factory:
 * </p>
 * <pre>
 * XFireFactory.register(PlexusXFireFactory.class, true);
 * </pre>
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PlexusXFireFactory
	extends XFireFactory
{
    private static PlexusXFireFactory standalone;

    protected Embedder embed;

    protected PlexusXFireFactory() 
    {
        try
        {
            URL resource = getPlexusConfiguration();
            
            embed = new Embedder();

            embed.setConfiguration( resource );

            Properties contextProperties = new Properties();

            embed.setProperties(contextProperties);
            
            embed.start();
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException("Couldn't load plexus embedder.", e);
        }
    }

    public static XFireFactory createInstance() 
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

    public XFire getXFire()
    {
        try
        {
            return (XFire) embed.lookup( XFire.ROLE );
        }
        catch (ComponentLookupException e)
        {
            throw new XFireRuntimeException("Couldn't lookup xfire component.", e);
        }
    }

	protected void finalize() throws Throwable
	{
		embed.stop();
        
        super.finalize();
	}
}
