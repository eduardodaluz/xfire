package org.codehaus.xfire.spring;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.transport.http.XFireServletController;
import org.springframework.beans.BeansException;
import org.springframework.web.servlet.FrameworkServlet;

/**
 * Provides tight integration between XFire and Spring.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XFireSpringServlet
    extends FrameworkServlet
{
    private XFire xfire;
    
    private XFireServletController controller;

    protected void initFrameworkServlet()
        throws ServletException, BeansException
    {
        super.initFrameworkServlet();

        if (!getWebApplicationContext().containsBean("xfire"))
        {
            throw new ServletException("Couldn't find spring bean 'xfire'.");
        }

        xfire = (XFire) getWebApplicationContext().getBean("xfire");
        
        controller = createController();
    }

    public XFire createXFire() 
        throws ServletException
    {
        try
        {
            XFireFactory factory = XFireFactory.newInstance();
            return factory.getXFire();
        }
        catch (Exception e)
        {
            throw new ServletException("Couldn't start XFire.", e);
        }
    }

    public XFireServletController createController() 
        throws ServletException
    {
        return new XFireServletController(xfire, getServletContext());
    }
    
    /**
     * @param req
     * @param res
     * @throws java.lang.Exception
     */
    protected void doService(HttpServletRequest req, HttpServletResponse res)
        throws Exception
    {
        controller.doService(req, res);
    }
}
