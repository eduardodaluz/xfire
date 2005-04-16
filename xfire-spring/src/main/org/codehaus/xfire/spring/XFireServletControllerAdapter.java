package org.codehaus.xfire.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.transport.http.XFireServletController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * An adapter for the {@link XFireServletController} so that it conforms to Springs MVC {@link Controller} interface.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class XFireServletControllerAdapter
        extends XFireServletController
        implements Controller
{
    private String serviceName;

    /**
     * Initializes a new instance of the adapter with the given XFire instance and service name.
     *
     * @param xfire       the XFire instance
     * @param serviceName the name of the service
     */
    public XFireServletControllerAdapter(XFire xfire, String serviceName)
    {
        super(xfire);
        this.serviceName = serviceName;
    }

    protected String getService(HttpServletRequest request)
    {
        return serviceName;
    }

    /**
     * Process the incoming SOAP request and create a SOAP response.
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @return <code>null</code>
     * @throws Exception in case of errors
     */
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        doService(request, response);
        return null;
    }
}
