package org.codehaus.xfire.spring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.BeanInvoker;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.spring.config.AbstractSoapBindingBean;
import org.codehaus.xfire.spring.config.EndpointBean;
import org.codehaus.xfire.spring.config.Soap11BindingBean;
import org.codehaus.xfire.spring.config.Soap12BindingBean;
import org.codehaus.xfire.wsdl11.builder.DefaultWSDLBuilderFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * A convenience bean which creates a Service from a ServiceFactory instance. If there is no
 * ServiceFactory set, ServiceBean will create one from the ObjectServiceFactory.
 * <p>
 * Alternatively, the <code>Jsr181BeanPostProcessor</code> or the 
 * <code>XFireExporter</code>may be used.
 * 
 * @see org.codehaus.xfire.service.Service
 * @see org.codehaus.xfire.spring.Jsr181BeanPostProcessor
 * @see org.codehaus.xfire.service.binding.ObjectServiceFactory
 * @see org.codehaus.xfire.spring.remoting.XFireExporter
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 * @org.xbean.XBean element="service"
 */
public class ServiceBean
    implements InitializingBean, ApplicationContextAware
{
    private final static Log logger = LogFactory.getLog(ServiceBean.class);

    private Service xfireService;

    private ServiceFactory serviceFactory;

    protected XFire xFire;

    private String name;

    private String namespace;

    private Class serviceInterface;

    private Object service;

    private List bindings;

    private List inHandlers;

    private List outHandlers;

    private List faultHandlers;

    private List schemas;
    
    protected Class implementationClass;

    private List properties = new ArrayList();

    /** Some properties to make it easier to work with ObjectServiceFactory */

    protected boolean createDefaultBindings;

    protected String use;

    protected String style;

    private String scope;
    

    public void afterPropertiesSet()
        throws Exception
    {
        // Use specific name if given, else fall back to bean name.
//        String theName = (this.name != null ? this.name : this.beanName);
//        if (theName != null && theName.startsWith("/"))
//        {
//            theName = theName.substring(1);
//        }
//        
        if (serviceFactory == null)
        {
            serviceFactory = new ObjectServiceFactory(xFire.getTransportManager(),
                                                      new AegisBindingProvider());
        }

        /**
         * Use the ServiceInterface if that is set, otherwise use the Class of 
         * the service object.
         */
        Class intf = getServiceClass();
        if (intf == null)
        {
            if (getServiceBean() == null)
                throw new RuntimeException("Error creating service " + name +
                        ". The service class or the service bean must be set!");
            
            intf = getServiceBean().getClass();
        }
        
        // Lets set up some properties for the service
        Map properties = new HashMap();
        
        if (createDefaultBindings)
            properties.put(ObjectServiceFactory.CREATE_DEFAULT_BINDINGS, Boolean.TRUE);
        
        if (style != null)
            properties.put(ObjectServiceFactory.STYLE, style);
        if (use != null)
            properties.put(ObjectServiceFactory.USE, use);
        
        if (implementationClass != null)
        {
            properties.put(ObjectInvoker.SERVICE_IMPL_CLASS, implementationClass);
        }
        
        // Set the properties 
        copyProperties(properties);
        
        xfireService = serviceFactory.create(intf, name, namespace, properties);

        if (bindings != null && serviceFactory instanceof ObjectServiceFactory)
        {
            initializeBindings();
        }
        
        if (logger.isInfoEnabled())
        {
            logger.info("Exposing service with name " + xfireService.getName());
        }

        // Register the service
        xFire.getServiceRegistry().register(xfireService);
        
        // If we're referencing a spring bean, set up our invoker.
        Object serviceBean = getProxyForService();
        if (serviceBean != null)
        {
            xfireService.setInvoker(new BeanInvoker(serviceBean));
        }

        if (schemas != null)
        {
            ObjectServiceFactory osf = (ObjectServiceFactory) serviceFactory;
            
            DefaultWSDLBuilderFactory wbf = 
                (DefaultWSDLBuilderFactory) osf.getWsdlBuilderFactory();
            wbf.setSchemaLocations(schemas);
        }

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
    }

    protected void initializeBindings()
        throws Exception
    {
        ObjectServiceFactory osf = (ObjectServiceFactory) serviceFactory;

        for (Iterator itr = bindings.iterator(); itr.hasNext();)
        {
            AbstractSoapBindingBean o = (AbstractSoapBindingBean) itr.next();
            org.codehaus.xfire.soap.AbstractSoapBinding binding = null;
            if (o instanceof Soap11BindingBean)
            {
                binding = osf.createSoap11Binding(xfireService, o.getName(), o.getTransport());
            }
            else if (o instanceof Soap12BindingBean)
            {
                binding = osf.createSoap12Binding(xfireService, o.getName(), o.getTransport());
            }
            
            binding.setUndefinedEndpointAllowed(o.isAllowUndefinedEndpoints());

            if (o.getEndpoints() == null) continue;
            
            for (Iterator eitr = o.getEndpoints().iterator(); eitr.hasNext();)
            {
                EndpointBean ep = (EndpointBean) eitr.next();
                
                osf.createEndpoint(xfireService, ep.getName(), ep.getUrl(), binding);
            }
        }
    }

    /**
     * @return
     */
    protected Object getProxyForService()
    {
        return getServiceBean();
    }

    /**
     * Gets the XFire Service created by this bean.
     * @return
     * @see Service
     */
    public Service getXFireService()
    {
        return xfireService;
    }

    /**
     * Gets the object which backs this service.
     * @return
     */
    public Object getServiceBean()
    {
        return service;
    }

    /**
     * Sets the object which backs this service.
     * @return
     */
    public void setServiceBean(Object service)
    {
        this.service = service;
    }

    /**
     * Set the service class. The service class is passed to the ServiceFactory's
     * create method and is used to determine the operations on the service.
     * @return
     */
    public Class getServiceClass()
    {
        return serviceInterface;
    }

    public void setServiceClass(Class serviceInterface)
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

    public List getSchemas()
    {
        return schemas;
    }

    public void setSchemas(List schemas)
    {
        this.schemas = schemas;
    }

    protected void copyProperties(Map properties)
    {
        Service service = getXFireService();
        for (Iterator iter = getProperties().iterator(); iter.hasNext();)
        {
            Object[] keyval = (Object[]) iter.next();
            String key = (String) keyval[0];
            Object value = keyval[1];
            
            properties.put(key, value);
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


    public List getBindings()
    {
        return bindings;
    }


    public void setBindings(List bindings)
    {
        this.bindings = bindings;
    }


    public boolean isCreateDefaultBindings()
    {
        return createDefaultBindings;
    }


    public void setCreateDefaultBindings(boolean createDefaultBindings)
    {
        this.createDefaultBindings = createDefaultBindings;
    }

}