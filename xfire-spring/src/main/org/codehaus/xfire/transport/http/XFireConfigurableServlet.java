package org.codehaus.xfire.transport.http;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireException;
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

	public XFire createXFire() throws ServletException 
	{
        configPath = getInitParameter(PARAM_CONFIG);
        XFire xfire;
        XFireConfigLoader loader = new XFireConfigLoader();
        try 
        {
			xfire = loader.loadConfig(getConfigPath(), getServletContext());
		} 
		catch (XFireException e) 
		{
			throw new ServletException(e);
		}
        if(xfire == null || xfire.getServiceRegistry() == null || xfire.getServiceRegistry().getServices() == null || xfire.getServiceRegistry().getServices().size() == 0)
        {
            xfire = super.createXFire();
        }

        return xfire;
    }

    
}
