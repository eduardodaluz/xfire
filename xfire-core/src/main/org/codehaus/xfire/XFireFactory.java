package org.codehaus.xfire;


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
public class XFireFactory
{
    private static XFireFactory standalone;
    
    private XFire xfire;
    
    private XFireFactory() 
        throws Exception
    {
        xfire = new DefaultXFire();
    }

	public static XFireFactory newInstance() 
        throws Exception
    {
        if (standalone == null)
		{
			synchronized (XFireFactory.class)
			{
				standalone = new XFireFactory();
			}
		}
		return standalone;
    }    
   
    public XFire getXFire() throws Exception
    {
        return xfire;
    }
}
