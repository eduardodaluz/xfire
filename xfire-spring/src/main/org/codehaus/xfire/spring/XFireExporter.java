package org.codehaus.xfire.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.handler.SoapHandler;
import org.codehaus.xfire.message.ObjectServiceHandler;
import org.codehaus.xfire.service.object.DefaultObjectService;
import org.codehaus.xfire.service.object.Invoker;
import org.codehaus.xfire.service.object.ServiceBuilder;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class XFireExporter
    extends RemoteExporter
    implements Controller, InitializingBean
{
    private BeanFactory factory;
    private DefaultObjectService service;
    private ServiceBuilder serviceBuilder;
    private SpringXFireController controller;
    private XFire xFire;
    
    public void afterPropertiesSet()
        throws Exception
    {
        service = (DefaultObjectService) getServiceBuilder().create(getServiceInterface());

        getXfire().getServiceRegistry().register( service );

        Invoker invoker = new BeanInvoker(getProxyForService());
        service.setServiceHandler(new SoapHandler(new ObjectServiceHandler(invoker)));
        
        controller = new SpringXFireController(getXfire(), service.getName());
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        controller.doService(request, response);
        
        return null;
    }

    public ServiceBuilder getServiceBuilder()
    {
        return serviceBuilder;
    }
    
    public void setServiceBuilder(ServiceBuilder serviceBuilder)
    {
        this.serviceBuilder = serviceBuilder;
    }
    
    public XFire getXfire()
    {
        return xFire;
    }

    public void setXfire(XFire xFire)
    {
        this.xFire = xFire;
    }
}