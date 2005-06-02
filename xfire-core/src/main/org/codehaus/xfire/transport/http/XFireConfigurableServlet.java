package org.codehaus.xfire.transport.http;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;
import org.codehaus.yom.Elements;
import org.codehaus.yom.stax.StaxBuilder;

/**
 * XFire Servlet as Dispatcher including a configuration<br>
 * of XFire from services.xml in classpath<br>
 * <p>
 */
public class XFireConfigurableServlet
    extends XFireServlet
{

    private final static String CONFIG_FILE = "META-INF/xfire/services.xml";

    private final static String ENCODING_STYLE_URI = "http://schemas.xmlsoap.org/soap/encoding/";

    private final static String SOAP_12 = "1.2";

    private Log log = LogFactory.getLog(XFireConfigurableServlet.class);

    private XFire xfire;
    
    /**
     * @see javax.servlet.Servlet#init()
     */
    public void init()
        throws ServletException
    {
        super.init();
        try
        {
            XFire xfire = XFireFactory.newInstance().getXFire();
            
            configureXFire(xfire);
        }
        catch (Exception e)
        {
            log.error("Couldn't configure XFire", e);
        }
    }

    private void configureXFire(XFire xfire)
        throws Exception
    {
        ServiceRegistry registry = xfire.getServiceRegistry();
        
        log.info("Searching for META-INF/xfire/services.xml");
        
        // get services.xml
        Enumeration en = getClass().getClassLoader().getResources(CONFIG_FILE);
        while (en.hasMoreElements())
        {
            URL resource = (URL) en.nextElement();
            
            loadServices( resource.openStream(), xfire.getTransportManager(), registry );
        }
    }

    private void loadServices(InputStream stream, TransportManager tman, ServiceRegistry registry) 
        throws Exception
    {
        try
        {
            XMLInputFactory ifactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = ifactory.createXMLStreamReader(stream);
            StaxBuilder builder = new StaxBuilder();
            Document doc = builder.build(reader);
            Element root = doc.getRootElement();
            
            Elements contents = root.getChildElements();
            for (int i = 0; i < contents.size(); i++)
            {
                Element element = contents.get(i);
                Elements services = element.getChildElements();
                for (int n = 0; n < services.size(); n++)
                {
                    Element service = services.get(n);
                    
                    loadService(service, tman, registry);
                }
            }
        }
        catch (XMLStreamException e1)
        {
            log.error("Could not parse META-INF/xfire/services.xml!", e1);
        }
    }

    private void loadService(Element service, TransportManager tman, ServiceRegistry registry) 
        throws ServletException
    {
        String name = getElementValue(service, "name", "");
        String namespace = getElementValue(service, "namespace", "");
        String style = getElementValue(service, "style", SoapConstants.STYLE_WRAPPED);
        String use = getElementValue(service, "use", SoapConstants.USE_LITERAL);
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
        ServiceFactory factory = loadServiceFactory(tman, 
                                                    bindingProvider, 
                                                    getElementValue(service, "serviceFactory", ""));
        
        Service svc = factory.create(clazz,
                                     name,
                                     namespace,
                                     soapVersion,
                                     style,
                                     use,
                                     null);
        
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
    }

    private ServiceFactory loadServiceFactory(TransportManager tman, BindingProvider bindingProvider, String serviceFactoryName)
    {
        ServiceFactory factory = null;
        if (serviceFactoryName.length() > 0)
        {
            try
            {
                factory = (ServiceFactory) loadClass(serviceFactoryName).newInstance();
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

    private BindingProvider loadBindingProvider(String bindingProviderName)
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
    private Class loadClass(String className)
        throws Exception
    {
        // Handle array'd types.
        if (className.endsWith("[]"))
        {
            className = "[L" + className.substring(0, className.length() - 2) + ";";
        }

        return getClass().getClassLoader().loadClass(className);
    }
}
