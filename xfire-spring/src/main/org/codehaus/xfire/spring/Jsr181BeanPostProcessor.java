package org.codehaus.xfire.spring;

import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.WebAnnotations;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.binding.BeanInvoker;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * This BeanPostProcessor will create an XFire service from a service class if and only if
 * it is marked with the JSR 181 @WebService attribute. This replaces the need for using the
 * ServiceComponent class.
 * 
 * @see org.codehaus.xfire.spring.ServiceBean
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class Jsr181BeanPostProcessor
    implements BeanPostProcessor
{
    private WebAnnotations annotations;

    private AnnotationServiceFactory serviceFactory;

    private ServiceRegistry registry;

    public Jsr181BeanPostProcessor(WebAnnotations annotations,
            AnnotationServiceFactory serviceFactory, ServiceRegistry registry)
    {
        this.annotations = annotations;
        this.serviceFactory = serviceFactory;
        this.registry = registry;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName)
        throws BeansException
    {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName)
        throws BeansException
    {
        Class clazz = bean.getClass();
        if (annotations.hasWebServiceAnnotation(clazz))
        {
            Service service = serviceFactory.create(clazz);
            service.getBinding().setInvoker(new BeanInvoker(bean));
            registry.register(service);
        }
        return bean;
    }
}
