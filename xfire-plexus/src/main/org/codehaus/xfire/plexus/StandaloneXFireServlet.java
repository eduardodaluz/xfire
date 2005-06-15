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
    {
        return factory.getXFire();
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
    {
        File config = new File(getWebappBase(), getInitParameter("config"));

        System.setProperty("xfire.config", config.getAbsolutePath());
        log("Configuration set to: " + config.getAbsolutePath());

        String plexusConfig = getInitParameter("plexus-config");
        if (plexusConfig != null)
        {
            System.setProperty("xfire.plexusConfig", plexusConfig);
        }

        return XFireFactory.newInstance().getXFire();
    }

    protected TransportManager getTransportManager()
    {
        return factory.getXFire().getTransportManager();
    }

    public ServiceRegistry getServiceRegistry()
    {
        return factory.getXFire().getServiceRegistry();
    }

    public void destroy()
    {
        factory = null;

        super.destroy();
    }
}
