package org.codehaus.xfire.spring;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.XFireFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * @author <a href="mailto:mikagoeckel@codehaus.org">Mika Goeckel</a>
 * 
 */
public class XFireConfigLoader
{
    private Log log = LogFactory.getLog(XFireConfigLoader.class);
    
    // we might want to move this one out if we want to cut the dependency on the javax.servlet package
    public XFire loadConfig(String configPath, ServletContext servletCtx) throws XFireException
    {
        ApplicationContext parent = null;
        
        if(servletCtx != null)
        {
            parent = (ApplicationContext)servletCtx.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        }

        ClassPathXmlApplicationContext newCtx = getXFireApplicationContext(configPath, parent);

        if((servletCtx != null) && (servletCtx.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) == null))
        {
             servletCtx.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, newCtx);
        }

        return getXFire(newCtx);
    }

    public XFire loadConfig(String configPath) throws XFireException
    {
        ClassPathXmlApplicationContext newCtx = getXFireApplicationContext(configPath, null);
        return getXFire(newCtx);
    }

    private XFire getXFire(ApplicationContext ctx)
    {
        XFire xfire = (XFire) ctx.getBean("xfire");
        log.debug("Setting XFire instance: "+xfire);
        
        // TODO: don't like this
        XFireFactory.newInstance().setXFire(xfire);

        return xfire;
    }

    /**
     * @param configPath may not be full
     * @param parent may be null
     * @throws XFireException  if the configuration file is missing
     */
    private ClassPathXmlApplicationContext getXFireApplicationContext(String configPath, ApplicationContext parent) throws XFireException
    {
        if(configPath == null)
        {
            throw new XFireException("Configuration file required");
        }
        
        ClassPathXmlApplicationContext newCtx = null;
        
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

        return newCtx;
    }
}
