package org.codehaus.xfire.transport.http;

import java.net.URL;
import java.util.Enumeration;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.spring.XFireConfigLoader;
import org.codehaus.xfire.util.XMLServiceBuilder;

/**
 * XFire Servlet as Dispatcher including a configuration<br>
 * of XFire from services.xml in classpath<br>
 * <p>
 */
public class XFireConfigurableServlet
    extends XFireServlet
{
    
    
    private final static String CONFIG_FILE = "META-INF/xfire/services.xml";

    private String configPath;
    
    private Log log = LogFactory.getLog(XFireConfigurableServlet.class);

    private boolean useNewConfig;

    
    /**
     * @return
     */
    private String getConfigPath(){
        if( configPath == null || configPath.length() == 0){
            return CONFIG_FILE;
        }
        return configPath;
    }
    /**
     * @see javax.servlet.Servlet#init()
     */
    public void init()
        throws ServletException
    {

        
        String useNewConfigStr = getInitParameter("useNewConfig");
        useNewConfig = Boolean.valueOf(useNewConfigStr).booleanValue();
        configPath = getInitParameter("config");
        log.error("Initializing servlet - [config:"+configPath+"][useNewConig"+useNewConfig+"]");
        try
        {

            if (!useNewConfig)
            {
                super.init();
                configureXFire();
            }
            else
            {
                configureXFireNew();
                controller = createController();
            }

        }
        catch (Exception e)
        {
            log.error("Couldn't configure XFire", e);
        }

    }

    protected void configureXFire()
        throws Exception
    {
        log.error("configureXFire...");
        XMLServiceBuilder builder = new XMLServiceBuilder(getXFire());
        Enumeration en = getClass().getClassLoader().getResources(getConfigPath());
        while (en.hasMoreElements())
        {
            log.error("Loading config...");
            URL resource = (URL) en.nextElement();

            builder.buildServices(resource.openStream());
        }

    }

    protected void configureXFireNew()
        throws Exception
    {
        // 
        log.info("Searching for " + getConfigPath());
        XFireConfigLoader loader = new XFireConfigLoader();
        xfire = loader.loadConfig(getConfigPath());

    }

}
