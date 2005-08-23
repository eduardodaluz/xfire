package org.codehaus.xfire.spring.remoting;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.xfire.spring.ServiceBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


/**
 * Web controller that exports the specified service bean as a XFire Soap service endpoint.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class XFireExporter
        extends ServiceBean
        implements Controller
{
    private XFireServletControllerAdapter delegate;
    
    public void afterPropertiesSet()
            throws Exception
    {
        super.afterPropertiesSet();
        
        delegate = new XFireServletControllerAdapter(getXfire(), 
                                                     getXFireService().getServiceInfo().getName());
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
        return delegate.handleRequest(request, response);
    }
}