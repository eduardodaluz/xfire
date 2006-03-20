package org.codehaus.xfire.transport.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;

/**
 * A servlet which processes incoming XFire requests.
 * It delegates to the XFireController to process the request.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 * @since Feb 13, 2004
 */
public class XFireServlet 
    extends HttpServlet
{
    protected XFire xfire;

    protected XFireServletController controller;

    private final static Log logger = LogFactory.getLog(XFireServlet.class);
    
    public final static String XFIRE_INSTANCE = "xfire.instance";
    
    public void init() 
        throws ServletException
    {
        try
        {
            super.init();
            xfire = createXFire();
            controller = createController();
        }
        catch (Throwable tx)
        {
            // log.er
            logger.error("Error initializing XFireServlet.", tx);
            throw new ServletException("Error initializing XFireServlet.", tx);
        }
    }

    /**
     * Get the xfire instance. if it hasn't been created, then {@link #createXFire()} will be called.
     */
    public XFire getXFire() throws ServletException
    {
        if (xfire == null) xfire = createXFire();
        
        return xfire;
    }

    /**
     * Get the xfire controller. if it hasn't been created, then {@link #createController()} will be called.
     */
    public XFireServletController getController() throws ServletException
    {
        if(controller == null) controller = createController();
        return controller;
    }

    public XFire createXFire() 
        throws ServletException
    {
        try
        {
            XFire xfire = (XFire) getServletContext().getAttribute(XFIRE_INSTANCE);
            if (xfire == null)
            {
                XFireFactory factory = XFireFactory.newInstance();
                xfire = factory.getXFire();
            }
            return xfire;
        }
        catch (Exception e)
        {
            throw new ServletException("Couldn't start XFire.", e);
        }
    }

    public XFireServletController createController() 
        throws ServletException
    {
        return new XFireServletController(xfire);
    }

    /**
     * Delegates to {@link XFireServletController#doService(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) 
        throws ServletException, IOException
    {
        controller.doService(request, response);
    }

    /**
     * Delegates to {@link XFireServletController#doService(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        controller.doService(req, res);
    }
}
