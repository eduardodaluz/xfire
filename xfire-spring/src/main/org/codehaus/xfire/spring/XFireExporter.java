package org.codehaus.xfire.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.object.DefaultObjectService;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.soap.SoapVersion;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


/**
 * Web controller that exports the specified service bean as a XFire Soap service endpoint.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class XFireExporter
        extends RemoteExporter
        implements Controller, InitializingBean
{
    private DefaultObjectService service;
    private ServiceFactory serviceBuilder;
    private SpringXFireController controller;
    private XFire xFire;
    private String style = SoapConstants.STYLE_WRAPPED;
    private String use = SoapConstants.USE_LITERAL;
    private SoapVersion soapVersion = Soap11.getInstance();


    public void afterPropertiesSet()
            throws Exception
    {
        service = (DefaultObjectService) getServiceBuilder().create(getServiceInterface(),
                                                                    getSoapVersion(),
                                                                    getStyle(),
                                                                    getUse());

        getXfire().getServiceRegistry().register(service);

        service.setInvoker(new BeanInvoker(getProxyForService()));

        controller = new SpringXFireController(getXfire(), service.getName());
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
        controller.doService(request, response);

        return null;
    }

    public ServiceFactory getServiceBuilder()
    {
        return serviceBuilder;
    }

    public void setServiceBuilder(ServiceFactory serviceBuilder)
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

    /**
     * Returns the style to use for service creation. This style is used by the exporter to {@link
     * ServiceFactory#create(Class, org.codehaus.xfire.soap.SoapVersion, String, String) create the service}. The
     * initial default style is {@link SoapConstants#STYLE_WRAPPED}.
     *
     * @return the style.
     */
    public String getStyle()
    {
        return style;
    }

    /**
     * Sets the style to use for service creation. This style is used by the exporter to {@link
     * ServiceFactory#create(Class, org.codehaus.xfire.soap.SoapVersion, String, String) create the service}. The
     * initial default style is {@link SoapConstants#STYLE_WRAPPED}.
     *
     * @param style the new style.
     */
    public void setStyle(String style)
    {
        this.style = style;
    }

    /**
     * Returns the use for service creation. This style is used by the exporter to {@link ServiceFactory#create(Class,
            * org.codehaus.xfire.soap.SoapVersion, String, String) create the service}. The initial default style is {@link
     * SoapConstants#USE_LITERAL}.
     *
     * @return the style.
     */
    public String getUse()
    {
        return use;
    }

    /**
     * Sets the use for service creation. This style is used by the exporter to {@link ServiceFactory#create(Class,
            * org.codehaus.xfire.soap.SoapVersion, String, String) create the service}. The initial default style is {@link
     * SoapConstants#USE_LITERAL}.
     *
     * @param use the new use
     */
    public void setUse(String use)
    {
        this.use = use;
    }

    /**
     * Returns the soap version to use for service creation. This style is used by the exporter to {@link
     * ServiceFactory#create(Class, org.codehaus.xfire.soap.SoapVersion, String, String) create the service}. The
     * initial default style is {@link Soap11}.
     *
     * @return the style.
     */
    public SoapVersion getSoapVersion()
    {
        return soapVersion;
    }

    /**
     * Sets the soap version to use for service creation. This style is used by the exporter to {@link
     * ServiceFactory#create(Class, org.codehaus.xfire.soap.SoapVersion, String, String) create the service}. The
     * initial default style is {@link Soap11}.
     *
     * @param soapVersion the new soap version
     */
    public void setSoapVersion(SoapVersion soapVersion)
    {
        this.soapVersion = soapVersion;
    }

}