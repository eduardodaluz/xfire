package org.codehaus.xfire.spring.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.spring.ServiceBean;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 * @org.xbean.XBean element="service"
 */
public class ServiceBeanWrapper
    extends ServiceBean
    implements ApplicationContextAware
{
    private SoapVersion soapVersion = Soap11.getInstance();

    private Class implementationClass;

    private String use;

    private String style;

    private String scope;

    private Class bindingProvider;

    private List properties = new ArrayList();

    public Class getImplementationClass()
    {
        return implementationClass;
    }

    public void setImplementationClass(Class implementationClass)
    {
        this.implementationClass = implementationClass;
    }

    public Class getBindingProvider()
    {
        return bindingProvider;
    }

    public void setBindingProvider(Class bindingProvider)
    {
        this.bindingProvider = bindingProvider;
    }

    public List getProperties()
    {
        return properties;
    }

    public void setProperties(List properties)
    {
        this.properties = properties;
    }

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }

    public String getStyle()
    {
        return style;
    }

    public void setStyle(String style)
    {
        this.style = style;
    }

    public String getUse()
    {
        return use;
    }

    public void setUse(String use)
    {
        this.use = use;
    }

    public SoapVersion getSoapVersion()
    {
        return soapVersion;
    }

    public void setSoapVersion(SoapVersion soapVersion)
    {
        this.soapVersion = soapVersion;
    }

    public Class getServiceClass()
    {
        return getServiceInterface();
    }

    public void setServiceClass(Class serviceClass)
    {
        setServiceInterface(serviceClass);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet()
        throws Exception
    {

        //BindingProvider bindingProviderImpl = loadBindingProvider(bindingProvider);
        BindingProvider bindingProviderImpl = (BindingProvider) (bindingProvider!=null?bindingProvider.newInstance():null);

        ServiceFactory serviceFactory = getServiceFactory();

        if (serviceFactory == null)
        {
            serviceFactory = new ObjectServiceFactory(xFire.getTransportManager(),
                    bindingProviderImpl);
            setServiceFactory(serviceFactory);
        }
        ObjectServiceFactory factory = (ObjectServiceFactory) serviceFactory;
        factory.setSoapVersion(soapVersion);
        if (style != null && style.length() > 0)
        {
            factory.setStyle(style);
        }
        if (use != null && use.length() > 0)
        {
            factory.setUse(use);
        }

        Object service = getService();

        if (service == null)
        {
            if (implementationClass != null)
            {
                service = implementationClass.newInstance();
                setService(service);
            }

        }
        super.afterPropertiesSet();

        copyProperites();
        
    }

    /**
     * 
     */
    private void copyProperites()
    {
        Service service = getXFireService();
        for (Iterator iter = getProperties().iterator(); iter.hasNext();)
        {

            Map.Entry entry = (Entry) iter.next();
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            service.setProperty(key, value);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext ctx)
        throws BeansException
    {
        xFire = (XFire) ctx.getBean("xfire");

    }

    /**
     * @param bindingProviderName
     * @return
     */
    /*protected BindingProvider loadBindingProvider(String bindingProviderName)
    {
        BindingProvider bindingProvider = null;
        if (bindingProviderName != null && bindingProviderName.length() > 0)
        {
            try
            {
                bindingProvider = (BindingProvider) loadClass(bindingProviderName).newInstance();
            }
            catch (Exception e)
            {
                throw new XFireRuntimeException("Could not load binding provider: "
                        + bindingProvider, e);
            }
        }
        return bindingProvider;
    }
*/
    /**
     * @param className
     * @return
     * @throws Exception
     */
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

    /**
     * @param annotationType
     * @param bindingProvider
     * @return
     * @throws Exception
     */
}
