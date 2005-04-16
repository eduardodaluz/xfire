package org.codehaus.xfire.spring;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.WebAnnotations;
import org.codehaus.xfire.service.object.BeanInvoker;
import org.codehaus.xfire.service.object.DefaultObjectService;
import org.codehaus.xfire.type.TypeMappingRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Implementation of the {@link org.springframework.web.servlet.HandlerMapping} interface that recognizes {@link
 * org.codehaus.xfire.annotations.WebServiceAnnotation web service annotations} on Spring-managed beans and
 * automatically wires them into the current servlet's WebApplicationContext.
 * <p/>
 * All beans Services are exposed by their {@link org.codehaus.xfire.annotations.WebServiceAnnotation#getServiceName()
 * service name}, with a defined {@link #setUrlPrefix(String) prefix}. For instance, a bean annotated with the service
 * name <code>EchoService</code> will be exposed as <code>/services/EchoService</code>.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @see WebAnnotations
 * @see org.codehaus.xfire.annotations.WebServiceAnnotation
 */
public class XFireWebAnnotationsHandlerMapping
        extends AbstractUrlHandlerMapping
{
    private WebAnnotations webAnnotations;
    private XFire xFire;
    private TypeMappingRegistry typeMappingRegistry;
    private String urlPrefix = "/services/";


    protected void initApplicationContext()
            throws BeansException
    {
        if (!(getApplicationContext() instanceof ConfigurableApplicationContext))
        {
            throw new ApplicationContextException("[" + getClass().getName() + "] needs to run in a ConfigurableApplicationContext");
        }
        ConfigurableListableBeanFactory beanFactory =
                ((ConfigurableApplicationContext) getApplicationContext()).getBeanFactory();

        String[] beanNames = getApplicationContext().getBeanDefinitionNames();

        AnnotationServiceFactory serviceFactory = new AnnotationServiceFactory(webAnnotations,
                                                                               xFire.getTransportManager(),
                                                                               typeMappingRegistry);

        // Take any bean name or alias that has a web service annotation
        for (int i = 0; i < beanNames.length; i++)
        {
            Class clazz = getApplicationContext().getType(beanNames[i]);
            if (webAnnotations.hasWebServiceAnnotation(clazz))
            {
                DefaultObjectService service = (DefaultObjectService) serviceFactory.create(clazz);
                if (logger.isInfoEnabled())
                {
                    logger.info("Exposing SOAP v." + service.getSoapVersion().getVersion() + " service " +
                                service.getName() + " to " + urlPrefix + service.getName() +
                                " as " + service.getStyle() + "/" + service.getUse());
                }
                xFire.getServiceRegistry().register(service);
                service.setInvoker(new BeanInvoker(beanFactory.getBean(beanNames[i])));
                Controller controller = new XFireServletControllerAdapter(xFire, service.getName());
                registerHandler(urlPrefix + service.getName(), controller);
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Rejected bean '" + beanNames[i] + "' since it has no WebService annotation");
                }
            }
        }
    }

    /**
     * Sets the web annotations implementation to use.
     */
    public void setWebAnnotations(WebAnnotations webAnnotations)
    {
        this.webAnnotations = webAnnotations;
    }

    /**
     * Sets the XFire instance.
     */
    public void setXfire(XFire xFire)
    {
        this.xFire = xFire;
    }

    /**
     * Sets the type mapping registry.
     */
    public void setTypeMappingRegistry(TypeMappingRegistry typeMappingRegistry)
    {
        this.typeMappingRegistry = typeMappingRegistry;
    }

    /**
     * Sets the url prefix used when exposing services. Defaults to <code>/services/</code>.
     */
    public void setUrlPrefix(String urlPrefix)
    {
        this.urlPrefix = urlPrefix;
    }
}
