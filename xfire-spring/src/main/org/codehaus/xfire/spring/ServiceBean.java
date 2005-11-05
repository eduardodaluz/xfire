package org.codehaus.xfire.spring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.AbstractBinding;
import org.codehaus.xfire.service.binding.BeanInvoker;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapVersion;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * A convenience bean which creates a Service from a ServiceFactory instance.
 * Alternatively, the Jsr181BeanPostProcessor may be used.
 * 
 * @see org.codehaus.xfire.service.Service
 * @see org.codehaus.xfire.spring.Jsr181BeanPostProcessor
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 * @org.xbean.XBean element="service"
 */
public class ServiceBean
    implements InitializingBean, BeanNameAware, ApplicationContextAware
{
    private final static Log logger = LogFactory.getLog(ServiceBean.class);

    private Service xfireService;

    private ServiceFactory serviceFactory;

    protected XFire xFire;

    private String name;

    private String namespace;

    private String beanName;

    private Class serviceInterface;

    private Object service;

    private List inHandlers;

    private List outHandlers;

    private List faultHandlers;

    protected SoapVersion soapVersion = Soap11.getInstance();

    protected Class implementationClass;

    protected String use;

    protected String style;

    private String scope;

    protected Class bindingProvider;

    private List properties = new ArrayList();

    public void afterPropertiesSet()
        throws Exception
    {
        // Use specific name if given, else fall back to bean name.
        String theName = (this.name != null ? this.name : this.beanName);
        if (theName != null && theName.startsWith("/"))
        {
            theName = theName.substring(1);
        }

        BindingProvider bindingProviderImpl = (BindingProvider) (bindingProvider != null ? bindingProvider
                .newInstance()
                : null);

        if (serviceFactory == null)
        {
            serviceFactory = new ObjectServiceFactory(xFire.getTransportManager(),
                    bindingProviderImpl);
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

        // If now service object set, try to create it from implementationClass
        if (service == null)
        {
            if (implementationClass != null)
            {
                service = implementationClass.newInstance();
            }else{
                throw new RuntimeException("Service object or implementationClass must be set!.");
            }
        }

        Class intf = getServiceInterface();
        if (intf == null)
            intf = getService().getClass();

        xfireService = serviceFactory.create(intf, theName, namespace, null);

        AbstractBinding binding = (AbstractBinding) xfireService.getBinding();
        if (logger.isInfoEnabled())
        {
            logger.info("Exposing SOAP v." + xfireService.getSoapVersion().getVersion()
                    + " service " + xfireService.getName() + " as " + binding.getStyle());
        }

        xFire.getServiceRegistry().register(xfireService);
        if (serviceInterface != null)
            binding.setInvoker(new BeanInvoker(getProxyForService()));
        else
            binding.setInvoker(new BeanInvoker(getService()));

        // set up in handlers
        if (xfireService.getInHandlers() == null)
            xfireService.setInHandlers(getInHandlers());
        else if (getInHandlers() != null)
            xfireService.getInHandlers().addAll(getInHandlers());

        // set up out handlers
        if (xfireService.getOutHandlers() == null)
            xfireService.setOutHandlers(getOutHandlers());
        else if (getOutHandlers() != null)
            xfireService.getOutHandlers().addAll(getOutHandlers());

        // set up fault handlers.
        if (xfireService.getFaultHandlers() == null)
            xfireService.setFaultHandlers(getFaultHandlers());
        else if (getFaultHandlers() != null)
            xfireService.getFaultHandlers().addAll(getFaultHandlers());

        copyProperites();
    }

    /**
     * @return
     */
    protected Object getProxyForService()
    {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.addInterface(getServiceInterface());

        proxyFactory.setTarget(getService());
        return proxyFactory.getProxy();
    }

    public Service getXFireService()
    {
        return xfireService;
    }

    public Object getService()
    {
        return service;
    }

    public void setService(Object service)
    {
        this.service = service;
    }

    public Class getServiceInterface()
    {
        return serviceInterface;
    }

    public void setServiceInterface(Class serviceInterface)
    {
        this.serviceInterface = serviceInterface;
    }

    public void setServiceFactory(ServiceFactory serviceFactory)
    {
        this.serviceFactory = serviceFactory;
    }

    public ServiceFactory getServiceFactory()
    {
        return this.serviceFactory;
    }

    /**
     * Sets the service name. Default is the bean name of this exporter.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Sets the service default namespace. Default is a namespace based on the
     * package of the {@link #getServiceInterface() service interface}.
     */
    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }

    public void setBeanName(String beanName)
    {
        this.beanName = beanName;
    }

    public List getFaultHandlers()
    {
        return faultHandlers;
    }

    public void setFaultHandlers(List faultHandlers)
    {
        this.faultHandlers = faultHandlers;
    }

    public List getInHandlers()
    {
        return inHandlers;
    }

    public void setInHandlers(List inHandlers)
    {
        this.inHandlers = inHandlers;
    }

    public List getOutHandlers()
    {
        return outHandlers;
    }

    public void setOutHandlers(List outHandlers)
    {
        this.outHandlers = outHandlers;
    }

    public void setXfire(XFire xFire)
    {
        this.xFire = xFire;
    }

    public XFire getXfire()
    {
        return xFire;
    }

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

    /**
     * 
     */
    protected void copyProperites()
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

}