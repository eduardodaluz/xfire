package org.codehaus.xfire;


/**
 * <p>
 * The XFireFactory class allows you to embed XFire within your
 * apps easily.
 * </p>
 * <p>
 * This class assumes one XFire instance per JVM. To create many 
 * XFire instances you must use your own configuration and instantiation
 * mechanism.
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
