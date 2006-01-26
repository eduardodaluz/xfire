package org.codehaus.xfire.spring;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.XFireFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.xbean.spring.context.ClassPathXmlApplicationContext;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * @author <a href="mailto:mikagoeckel@codehaus.org">Mika Goeckel</a>
 * 
 */
public class XFireConfigLoader
{
    private Log log = LogFactory.getLog(XFireConfigLoader.class);
    
    public XFire loadConfig(String configPath, ServletContext servletCtx) throws XFireException
    {
    	if(configPath == null)
    	{
    		throw new XFireException("Configuration file required");
    	}
    	
    	ApplicationContext parent = null;
    	ClassPathXmlApplicationContext newCtx = null;
    	
    	if(servletCtx != null)
    	{
    		parent = (ApplicationContext)servletCtx.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    	}

    	String configFiles[] = null;
    	
    	if((parent == null) || !parent.containsBean("xfire"))
		{
    		// Include all xfire definitions (including custom editors)
    		configPath = "org/codehaus/xfire/spring/xfire.xml," + configPath;
		} 
    	else 
    	{
    		// Include only custom editors, because they are nor inherited from springs parent defs.
			configPath = "org/codehaus/xfire/spring/customEditors.xml," + configPath;
    	}

        if(configPath.indexOf(",") != -1)
        {
            configFiles = configPath.split(",");
        } 
        else 
        {
        	configFiles = new String[]{configPath};
        }
        
		if(parent != null)
    	{
	       	newCtx = new ClassPathXmlApplicationContext(configFiles, parent);    		
    	} 
    	else 
    	{
    		newCtx = new ClassPathXmlApplicationContext(configFiles);
    	}
    				
    	if((servletCtx != null) && (servletCtx.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) == null))
		{
    		servletCtx.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, newCtx);
		}
        	
        XFire xfire = (XFire) newCtx.getBean("xfire");
        log.debug("Setting XFire instance: "+xfire);
        
        // TODO: don't like this
        XFireFactory.newInstance().setXFire(xfire);

        return xfire;
    }

}
