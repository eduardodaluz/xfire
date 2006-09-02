package org.codehaus.xfire.spring;

import javax.servlet.ServletContext;

import org.apache.xbean.spring.context.SpringApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

public class WebConfigLoader extends XFireConfigLoader
{
    private ServletContext servletContext;
    
    public WebConfigLoader(ServletContext servletContext)
    {
        super();
        this.servletContext = servletContext;
    }

    protected org.springframework.context.support.GenericApplicationContext createContext(ApplicationContext parent)
    {
        GenericWebApplicationContext ctx = new GenericWebApplicationContextX();
        ctx.setParent(parent);
        ctx.setServletContext(servletContext);
        
        return ctx;
    }

    public class GenericWebApplicationContextX extends GenericWebApplicationContext
        implements SpringApplicationContext
    {
        
    }
}
