package org.codehaus.xfire.spring.config;

import java.lang.reflect.Constructor;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class ServiceFactoryBean
    implements FactoryBean
{

    private static final String JSR181_FACTORY = "jsr181";

    private static final String COMMONS_FACTORY = "commons-attributes";

    private String name;

    private BindingProvider bindingProvider;

    private TransportManager transportManager;

    ObjectServiceFactory factory;

    public ServiceFactoryBean(String name)
    {
        this.name = name;
    }

    public TransportManager getTransportManager()
    {
        return transportManager;
    }

    public void setTransportManager(TransportManager transportManager)
    {
        this.transportManager = transportManager;
    }

    public BindingProvider getBindingProvider()
    {
        return bindingProvider;
    }

    public void setBindingProvider(BindingProvider bindingProvider)
    {
        this.bindingProvider = bindingProvider;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject()
        throws Exception
    {
        return factory;
    }

    /**
     * @org.xbean.InitMethod
     * @throws Exception
     */
    public void initialize()
        throws Exception
    {
        String serviceFactory = name;
        if (JSR181_FACTORY.equals(serviceFactory) || COMMONS_FACTORY.equals(serviceFactory))
            factory = getAnnotationServiceFactory(serviceFactory, bindingProvider);
        else
            factory = loadServiceFactory(serviceFactory, bindingProvider);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class getObjectType()
    {
        return factory.getClass();
    }

    public boolean isSingleton()
    {
        return false;
    }

    /**
     * @param annotationType
     * @param bindingProvider
     * @return
     * @throws Exception
     */
    protected ObjectServiceFactory getAnnotationServiceFactory(String annotationType,
                                                               BindingProvider bindingProvider)
        throws Exception
    {
        Class annotsClz = null;
        Class clz = loadClass("org.codehaus.xfire.annotations.AnnotationServiceFactory");

        if (JSR181_FACTORY.equals(annotationType))
        {
            annotsClz = loadClass("org.codehaus.xfire.annotations.jsr181.Jsr181WebAnnotations");
        }
        else if (COMMONS_FACTORY.equals(annotationType))
        {
            annotsClz = loadClass("org.codehaus.xfire.annotations.commons.CommonsWebAttributes");
        }

        Class webAnnot = loadClass("org.codehaus.xfire.annotations.WebAnnotations");

        Constructor con = clz.getConstructor(new Class[] { webAnnot, TransportManager.class,
                BindingProvider.class });

        return (ObjectServiceFactory) con.newInstance(new Object[] { annotsClz.newInstance(),
                getTransportManager(), bindingProvider });
    }

    /**
     * @param bindingProvider
     * @param serviceFactoryName
     * @return
     */
    protected ObjectServiceFactory loadServiceFactory(String serviceFactoryName,
                                                      BindingProvider bindingProvider)
    {
        ObjectServiceFactory factory = null;
        if (serviceFactoryName.length() > 0)
        {
            // Attempt to load a ServiceFactory for the user.
            try
            {
                Class clz = loadClass(serviceFactoryName);
                TransportManager tman = getTransportManager();

                Constructor con = null;
                Object[] arguments = null;

                try
                {
                    con = clz.getConstructor(new Class[] { TransportManager.class,
                            BindingProvider.class });
                    arguments = new Object[] { tman, bindingProvider };
                }
                catch (NoSuchMethodException e)
                {
                    try
                    {
                        con = clz.getConstructor(new Class[] { TransportManager.class });
                        arguments = new Object[] { tman };
                    }
                    catch (NoSuchMethodException e1)
                    {
                        con = clz.getConstructor(new Class[0]);
                        arguments = new Object[0];
                    }
                }

                return (ObjectServiceFactory) con.newInstance(arguments);
            }
            catch (Exception e)
            {
                throw new XFireRuntimeException("Could not load service factory: "
                        + serviceFactoryName, e);
            }
        }
        else
        {
            factory = new ObjectServiceFactory(getTransportManager(), bindingProvider);
        }

        return factory;
    }

    protected Class loadClass(String className)
        throws Exception
    {
        // Handle array'd types.
        if (className.endsWith("[]"))
        {
            className = "[L" + className.substring(0, className.length() - 2) + ";";
        }

        return ClassLoaderUtils.loadClass(className, getClass());
    }

}
