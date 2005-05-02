package org.codehaus.xfire.plexus;

import java.io.File;
import javax.servlet.ServletException;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.transport.http.XFireServlet;

/**
 * Creates an embedded version of XFire within a servlet.  For most applications this will probably be sufficient.  For
 * more advanced container usages, see the XFireServlet and Plexus documentation.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class StandaloneXFireServlet
        extends XFireServlet
{
    XFireFactory factory;

    private File webInfPath;

    static
    {
        // register the PlexusXFireFactory
        XFireFactory.registerFactory(PlexusXFireFactory.class, true);
    }

    public XFire getXFire()
            throws ServletException
    {
        try
        {
            return factory.getXFire();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ServletException("Couldn't find XFire service.  Check configuration.", e);
        }
    }

    public File getWebappBase()
    {
        if (webInfPath == null)
        {
            webInfPath = new File(getServletContext().getRealPath("/WEB-INF"));
        }

        return webInfPath;
    }

    public XFire createXFire()
            throws ServletException
    {
        File config = new File(getWebappBase(), getInitParameter("config"));

        System.setProperty("xfire.config", config.getAbsolutePath());
        log("Configuration set to: " + config.getAbsolutePath());

        String plexusConfig = getInitParameter("plexus-config");
        if (plexusConfig != null)
        {
            System.setProperty("xfire.plexusConfig", plexusConfig);
        }

        try
        {
            return XFireFactory.newInstance().getXFire();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ServletException("Couldn't start XFire service.  Check configuration.", e);
        }
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
            return factory.getXFire().getTransportManager();
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
            return factory.getXFire().getServiceEndpointRegistry();
        }
        catch (Exception e)
        {
            throw new ServletException("No service registry found!", e);
        }
    }

    public void destroy()
    {
        factory = null;

        super.destroy();
    }
}
