package org.codehaus.xfire.plexus;

import java.io.File;
import javax.servlet.ServletException;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.TransportManager;

/**
 * Creates an embedded version of XFire within a servlet.  For most
 * applications this will probably be sufficient.  For more advanced
 * container usages, see the XFireServlet and Plexus documentation.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class StandaloneXFireServlet
	extends XFireServlet
{
    StandaloneXFire xfire;
    
    public XFire getXFire() throws ServletException
    {
        try
        {
            return xfire.getXFire();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ServletException("Couldn't find XFire service.  Check configuration.", e);
        }
    }
    
    public void init() throws ServletException
    {
        File config = new File(getWebappBase(), getInitParameter("config"));
        
        System.setProperty("xfire.config", config.getAbsolutePath());
        
        String plexusConfig = getInitParameter("plexus-config");
        if ( plexusConfig != null )
        {
        	System.setProperty("xfire.plexusConfig", plexusConfig);
        }
        
        try
        {
            xfire = StandaloneXFire.getInstance();
        }       
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ServletException("Couldn't start XFire service.  Check configuration.", e);
        }
        
        super.init();
    }
    
    /**
     * @return
     * @throws Exception
     */
    protected TransportManager getTransportManager()
    	throws ServletException
    {
        try
        {
            return xfire.getTransportService();
        }
        catch (Exception e)
        {
            throw new ServletException("No transport service found!", e);
        }
    }
    
    public ServiceRegistry getServiceRegistry()
        throws ServletException
    {
        try
        {
            return xfire.getServiceRegistry();
        }
        catch (Exception e)
        {
            throw new ServletException("No service registry found!", e);
        }
    }
    
    public void destroy()
    {
        xfire = null;

        super.destroy();
    }
}
