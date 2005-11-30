package org.codehaus.xfire.util;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Builds services from an xml configuration file.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XMLServiceBuilder
{
    private static final Log log = LogFactory.getLog(XMLServiceBuilder.class);

    private static final Object DEFAULT_FACTORY_INSTANCE = new DefaultFactory();

    private static Method DEFAULT_FACTORY_METHOD = null;

    /**
     * It's likely that the same factory object will be used for creation for more then only 1 object, so cache it. 
     */
    private Map factoryCache = new HashMap();
    
    private XFire xfire;

    public XMLServiceBuilder(XFire xfire)
    {
        this.xfire = xfire;
        try
        {
            DEFAULT_FACTORY_METHOD = DefaultFactory.class.getMethod("create",new Class[]{String.class});
        }
        catch (SecurityException e)
        {
            //  Imposible :)
            log.error(e);
        }
        catch (NoSuchMethodException e)
        {
            // Imposible :)
            log.error(e);       
        }
    }
    
    protected XFire getXFire()
    {
        return xfire;
    }

    /**
     * Returns a collection of SOAP services.
     * <p> 
     * This method takes an input stream and for each service element 
     * builds a SOAP service.  The stream is interrogated for the following
     * element values:  name, namespace, style, use, serviceClass, 
     * implementationClass, bindingProvider, and 
     * property (a repeatable element) using attribute 'key'
     * <p>
     * @param  stream      A xml-based resource bundle
     * @return Collection  A collection of services prescribed in the bundle
     */   
    public Collection buildServices(InputStream stream)
        throws Exception
    {
        try
        {
            XMLInputFactory ifactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = ifactory.createXMLStreamReader(stream);
            StaxBuilder builder = new StaxBuilder();
            Document doc = builder.build(reader);
            Element root = doc.getRootElement();

            List serviceList = new ArrayList();
            List contents = root.getChildren("services");
            for (int i = 0; i < contents.size(); i++)
            {
                Element element = (Element) contents.get(i);
                List services = element.getChildren();
                for (int n = 0; n < services.size(); n++)
                {
                    Element service = (Element) services.get(n);

                    serviceList.add(loadService(service));
                }
            }
            return serviceList;
        }
        catch (XMLStreamException e1)
        {
            log.error("Could not parse META-INF/xfire/services.xml!", e1);
            throw e1;
        }
    }

    /**
     * Keeps data about object factory. 
     * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
     *
     */
    private class ObjectFactory
    {
        /**
         * Instance of object declared as factory.
         */
        private Object factoryInstance;

        /**
         * Method declared as factory method.
         */
        private Method factoryMethod;

        /**
         * @param factory
         * @param method
         */
        public ObjectFactory(final Object factory,final Method method){
            factoryInstance = factory;
            factoryMethod = method;
        }
        
        /**
         * Creates object given by className param using factory object instance @see factoryInstance.
         * @param className
         * @return created object.
         * @throws Exception
         */
        public Object createObject(String className)
            throws Exception
        {
            try{
            return factoryMethod.invoke(factoryInstance, new Object[] { className });
            }catch(Exception e){
                throw new XFireRuntimeException("Coundn't create instance of object :"+className,e);
            }
        }
        
        
    }

    /**
     * Returns ObjectFactory object created from values defined as attribues for given Element or null if no required
     * attributes are not present.
     * @param element
     * @return
     * @throws Exception
     */
    private ObjectFactory getObjectFactory(Element element)
        throws Exception
    {
        
        Method factoryMethod  = null;
        Object factoryInstance = null;
        
        String factoryClassName = element.getAttributeValue("factory-class");
        String factoryMethodName = element.getAttributeValue("factory-method");
        
        // Both factoryClassName and factoryNethodName must be provided.
        if (factoryClassName != null && factoryClassName.length() > 0 && factoryMethodName != null
                && factoryMethodName.length() > 0)
        {

            // Check if factory instance was created before..
            factoryInstance = factoryCache.get(factoryClassName);
            if (factoryInstance == null)
            {
                factoryInstance = loadClass(factoryClassName).newInstance();
                factoryCache.put(factoryClassName, factoryInstance);
            }
         
         factoryMethod  = factoryInstance.getClass().getMethod(factoryMethodName,new Class[]{String.class});
        
        }else{
            
            // No data for factory is provied so use default to avoid if(factory == null ) new MyClass() code
            factoryInstance = DEFAULT_FACTORY_INSTANCE;
            factoryMethod = DEFAULT_FACTORY_METHOD;
        }
        
        return new ObjectFactory(factoryInstance, factoryMethod);
    }

    protected Service loadService(Element service)
        throws Exception
    {
        ServiceRegistry registry = getXFire().getServiceRegistry();

        String name = getElementValue(service, "name", null);
        String namespace = getElementValue(service, "namespace", null);
        String style = getElementValue(service, "style", "");
        String use = getElementValue(service, "use", "");
        String serviceClass = getElementValue(service, "serviceClass", "");
        String implClassName = getElementValue(service, "implementationClass", "");
        String bindingProviderName = getElementValue(service, "bindingProvider", "");

        String soapVersionValue = getElementValue(service, "soapVersion", "1.1");
        SoapVersion soapVersion;
        if (soapVersionValue.equals("1.2"))
        {
            soapVersion = Soap12.getInstance();
        }
        else
        {
            soapVersion = Soap11.getInstance();
        }

        Class clazz = null;
        try
        {
            clazz = loadClass(serviceClass);
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException("Could not load service class: " + serviceClass, e);
        }

        BindingProvider bindingProvider = loadBindingProvider(bindingProviderName);

        String serviceFactory = getElementValue(service, "serviceFactory", "");
        ObjectServiceFactory factory;
        if (serviceFactory.equals("jsr181") || serviceFactory.equals("commons-attributes"))
            factory = getAnnotationServiceFactory(serviceFactory, bindingProvider);
        else
            factory = loadServiceFactory(bindingProvider, serviceFactory);

        if (style.length() > 0)
            factory.setStyle(style);
        if (use.length() > 0)
            factory.setUse(use);

        factory.setSoapVersion(soapVersion);

        Service svc = null;
        if (name != null || namespace != null)
        {
            svc = factory.create(clazz, name, namespace, null);
        }
        else
        {
            svc = factory.create(clazz);
        }

        if (implClassName.length() > 0)
        {
            Class implClazz = null;
            try
            {
                implClazz = loadClass(implClassName);
            }
            catch (Exception e)
            {
                throw new XFireRuntimeException("Could not load implementation class: "
                        + serviceClass, e);
            }

            svc.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, implClazz);

            if (log.isInfoEnabled())
            {
                log.info("Created Service " + name + " with impl " + implClazz + ", soap version: "
                        + soapVersionValue + ", style: " + style + ", use: " + use + ", namespace "
                        + svc.getTargetNamespace());
            }
        }
        else
        {
            if (log.isInfoEnabled())
            {
                log.info("Created Service " + name + " with impl " + clazz + ", soap version: "
                        + soapVersionValue + ", style: " + style + ", use: " + use + ", namespace "
                        + svc.getTargetNamespace());
            }
        }

        loadServiceProperties(svc,service);

        svc.getInHandlers().addAll(createHandlers(service.getChild("inHandlers")));
        svc.getOutHandlers().addAll(createHandlers(service.getChild("outHandlers")));
        svc.getFaultHandlers().addAll(createHandlers(service.getChild("faultHandlers")));

        registry.register(svc);

        return svc;
    }

    protected ObjectServiceFactory loadServiceFactory(BindingProvider bindingProvider,
                                                      String serviceFactoryName)
    {
        ObjectServiceFactory factory = null;
        if (serviceFactoryName.length() > 0)
        {
            // Attempt to load a ServiceFactory for the user.
            try
            {
                Class clz = loadClass(serviceFactoryName);
                TransportManager tman = getXFire().getTransportManager();

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
            factory = new ObjectServiceFactory(getXFire().getTransportManager(), bindingProvider);
        }

        return factory;
    }

    protected ObjectServiceFactory getAnnotationServiceFactory(String annotationType,
                                                               BindingProvider bindingProvider)
        throws Exception
    {
        Class annotsClz = null;
        Class clz = loadClass("org.codehaus.xfire.annotations.AnnotationServiceFactory");

        if (annotationType.equals("jsr181"))
        {
            annotsClz = loadClass("org.codehaus.xfire.annotations.jsr181.Jsr181WebAnnotations");
        }
        else if (annotationType.equals("commons-attributes"))
        {
            annotsClz = loadClass("org.codehaus.xfire.annotations.commons.CommonsWebAttributes");
        }

        Class webAnnot = loadClass("org.codehaus.xfire.annotations.WebAnnotations");

        Constructor con = clz.getConstructor(new Class[] { webAnnot, TransportManager.class,
                BindingProvider.class });

        return (ObjectServiceFactory) con.newInstance(new Object[] { annotsClz.newInstance(),
                getXFire().getTransportManager(), bindingProvider });
    }

    protected BindingProvider loadBindingProvider(String bindingProviderName)
    {
        BindingProvider bindingProvider = null;
        if (bindingProviderName.length() > 0)
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

    private List createHandlers(Element child)
        throws Exception
    {
        if (child == null)
            return Collections.EMPTY_LIST;

        List handlers = child.getChildren("handler");
        if (handlers.size() == 0)
            return Collections.EMPTY_LIST;

        List pipe = new ArrayList();

        for (int i = 0; i < handlers.size(); i++)
        {
            pipe.add(getHandler((Element) handlers.get(i)));
        }

        return pipe;
    }

    /**
     * @param element
     * @return
     * @throws Exception
     */
    protected Handler getHandler(Element element)
        throws Exception
    {
        String handlerClassName = element.getValue();
        return (Handler) getObjectFactory(element).createObject(handlerClassName);
    }

    public String getElementValue(Element root, String name, String def)
    {
        Element child = root.getChild(name);
        if (child != null)
        {
            String value = child.getValue();
            if (value != null && value.length() > 0)
                return value;
        }

        return def;
    }
    
    private void loadServiceProperties(Service svc, Element child)
    {
        List elements = child.getChildren("property");
        if (elements.size() == 0)
            return;
        
        for (int i = 0; i < elements.size(); i++)
        {
            Element element = (Element) elements.get(i);
            String key = element.getAttributeValue("key");
            String value = element.getValue();
            if (key.length() > 1 && value.length() > 1)
                svc.setProperty(key, value);
        }
    }
    
    /**
     * Load a class from the class loader.
     * 
     * @param className
     *            The name of the class.
     * @return The class.
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
    
    private static class DefaultFactory
    {
        public Object create(String className)
            throws Exception
        {
            // Handle array'd types.
            if (className.endsWith("[]"))
            {
                className = "[L" + className.substring(0, className.length() - 2) + ";";
            }

            return ClassLoaderUtils.loadClass(className, getClass()).newInstance();
        }
    }
}

