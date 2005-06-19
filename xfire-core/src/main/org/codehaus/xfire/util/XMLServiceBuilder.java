package org.codehaus.xfire.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;
import org.codehaus.yom.Elements;
import org.codehaus.yom.stax.StaxBuilder;

/**
 * Builds services from an xml configuration file.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XMLServiceBuilder
{
    private static final Log log = LogFactory.getLog(XMLServiceBuilder.class);
    
    private XFire xfire;
    
    public XMLServiceBuilder(XFire xfire)
    {
        this.xfire = xfire;
    }
    
    protected XFire getXFire()
    {
        return xfire;
    }

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
            Elements contents = root.getChildElements();
            for (int i = 0; i < contents.size(); i++)
            {
                Element element = contents.get(i);
                Elements services = element.getChildElements();
                for (int n = 0; n < services.size(); n++)
                {
                    Element service = services.get(n);
                    
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

    protected Service loadService(Element service) 
        throws Exception
    {
        ServiceRegistry registry = getXFire().getServiceRegistry();
        TransportManager tman = getXFire().getTransportManager();
        
        String name = getElementValue(service, "name", "");
        String namespace = getElementValue(service, "namespace", "");
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
        ObjectServiceFactory factory =
            loadServiceFactory(tman, bindingProvider, getElementValue(service, "serviceFactory", ""));
        
        if (style.length() > 0) factory.setStyle(style);
        if (use.length() > 0) factory.setUse(use);
        
        factory.setSoapVersion(soapVersion);
        
        Service svc = null;
        if (name.length() > 0 || namespace.length() > 0)
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
                throw new XFireRuntimeException("Could not load implementation class: " + serviceClass, e);
            }
            
            svc.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, implClazz);
            
            if (log.isInfoEnabled())
            {
                log.info("Created Service " + name + " with impl " + implClazz
                         + ", soap version: " + soapVersionValue + ", style: " + style + ", use: "
                         + use + ", namespace " + svc.getServiceInfo().getName().getNamespaceURI());
            }
        }
        else
        {
            if (log.isInfoEnabled())
            {
                log.info("Created Service " + name + " with impl " + clazz
                         + ", soap version: " + soapVersionValue + ", style: " + style + ", use: "
                         + use + ", namespace " + svc.getServiceInfo().getName().getNamespaceURI());
            }
        }
        
        registry.register(svc);
        
        return svc;
    }

    protected ObjectServiceFactory loadServiceFactory(TransportManager tman,
                                                      BindingProvider bindingProvider,
                                                      String serviceFactoryName)
    {
        ObjectServiceFactory factory = null;
        if (serviceFactoryName.length() > 0)
        {
            try
            {
                factory = (ObjectServiceFactory) loadClass(serviceFactoryName).newInstance();
            }
            catch (Exception e)
            {
                throw new XFireRuntimeException("Could not load service factory: " + serviceFactoryName, e);
            }
        }
        else
        {
            factory = new ObjectServiceFactory(tman, bindingProvider);
        }
        
        return factory;
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
                throw new XFireRuntimeException("Could not load binding provider: " + bindingProvider, e);
            }
        }
        return bindingProvider;
    }

    public String getElementValue(Element root, String name, String def)
    {
        Element child = root.getFirstChildElement(name);
        if (child != null)
        {
            String value = child.getValue();
            if (value != null && value.length() > 0)
                return value;
        }
        
        return def;
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
}
