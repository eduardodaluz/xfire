package org.codehaus.xfire.spring.remoting;

import java.lang.reflect.Modifier;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.WebAnnotations;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.binding.BeanInvoker;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanIsAbstractException;
import org.springframework.context.ApplicationContext;
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
public class Jsr181HandlerMapping
        extends AbstractUrlHandlerMapping
{
    private WebAnnotations webAnnotations;
    private XFire xFire;
    private TypeMappingRegistry typeMappingRegistry;
    private String urlPrefix = "/services/";

    protected void initApplicationContext()
            throws BeansException
    {
        AnnotationServiceFactory serviceFactory =
                new AnnotationServiceFactory(webAnnotations,
                                             xFire.getTransportManager(),
                                             new AegisBindingProvider(typeMappingRegistry));

        ApplicationContext context = getApplicationContext();
 
        while (true)
        {
            if (context == null) break;
            
            processBeans(context, serviceFactory);

            context = context.getParent();
        }
    }

    private void processBeans(ApplicationContext beanFactory, AnnotationServiceFactory serviceFactory)
    {
        String[] beanNames = beanFactory.getBeanDefinitionNames();

        // Take any bean name or alias that has a web service annotation
        for (int i = 0; i < beanNames.length; i++)
        {
            Class clazz;
            Object bean;
            try
            {
                clazz = getApplicationContext().getType(beanNames[i]);
                bean = beanFactory.getBean(beanNames[i]);
            }
            catch (BeanIsAbstractException e)
            {
                // The bean is abstract, we won't be doing anything with it.
                continue;
            }

            if (clazz != null && 
                    !Modifier.isAbstract(clazz.getModifiers()) && 
                    webAnnotations.hasWebServiceAnnotation(clazz))
            {
                Service endpoint = serviceFactory.create(clazz);
                ServiceInfo service = endpoint.getServiceInfo();
                if (logger.isInfoEnabled())
                {
                    logger.info("Exposing  service " + endpoint.getName() + 
                                " to " + urlPrefix + endpoint.getSimpleName());
                }
                
                xFire.getServiceRegistry().register(endpoint);
                endpoint.setInvoker(new BeanInvoker(bean));
                Controller controller = new XFireServletControllerAdapter(xFire, endpoint.getName());
                registerHandler(urlPrefix + endpoint.getSimpleName(), controller);
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
