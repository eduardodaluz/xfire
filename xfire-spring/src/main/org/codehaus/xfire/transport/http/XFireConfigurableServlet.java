package org.codehaus.xfire.transport.http;

import java.util.Enumeration;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.spring.XFireConfigLoader;

/**
 * XFire Servlet as Dispatcher including a configuration<br>
 * of XFire from services.xml in classpath<br>
 * <p>
 */
public class XFireConfigurableServlet
    extends XFireServlet
{
    private static Log log = LogFactory.getLog(XFireConfigurableServlet.class);    
    
    private final static String CONFIG_FILE = "META-INF/xfire/services.xml";

    private final static String PARAM_CONFIG="config";
    
    /**
     * Path to configuration file 
     */
    private String configPath;
    
    /**
     * @return
     */
    private String getConfigPath()
    {
        if (configPath == null || configPath.length() == 0)
        {
            return CONFIG_FILE;
        }
        return configPath;
    }

    public XFire createXFire()
        throws ServletException
    {
        configPath = getInitParameter(PARAM_CONFIG);
        Enumeration en ;
        
        try
        {
            en = getClass().getClassLoader().getResources(getConfigPath());
            if (!en.hasMoreElements())
            {
                log.warn("Can't find any configuration file : " + getConfigPath());
            }
            
            log.debug("Found services.xml at "+getConfigPath());
            
            XFire xfire;
            if (en.hasMoreElements())
            {
                XFireConfigLoader loader = new XFireConfigLoader();
                xfire = loader.loadConfig(getConfigPath());
            }
            else
            {
                xfire = super.createXFire();
            }

            log.debug("Done loading configuration.");
            
            return xfire;
        }
        catch (Exception e)
        {
            log.error("Couldn't configure XFire", e);
            throw new ServletException("Coudln't configure XFire.", e);
        }
    }

    
}
