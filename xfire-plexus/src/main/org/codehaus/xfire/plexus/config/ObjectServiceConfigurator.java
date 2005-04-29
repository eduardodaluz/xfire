package org.codehaus.xfire.plexus.config;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.fault.FaultHandler;
import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.plexus.PlexusXFireComponent;
import org.codehaus.xfire.plexus.ServiceInvoker;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.Invoker;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.transport.TransportManager;

/**
 * Creates and configures services.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 20, 2004
 */
public class ObjectServiceConfigurator
        extends PlexusXFireComponent
        implements Configurator
{
    public Service createService(PlexusConfiguration config)
            throws Exception
    {
        ServiceFactory builder = getServiceFactory(config);

        String name = config.getChild("name").getValue();
        String namespace = config.getChild("namespace").getValue("");
        String use = config.getChild("use").getValue("literal");
        String style = config.getChild("style").getValue("wrapped");
        String serviceClass = config.getChild("serviceClass").getValue();
        String implClass = config.getChild("implementationClass").getValue("");
        String soapVersion = config.getChild("soapVersion").getValue("1.1");
        String wsdlUrl = config.getChild("wsdl").getValue("");

        Service service = null;
        if (wsdlUrl.length() > 0)
        {
            try
            {
                TypeMapping tm = getTypeMappingRegistry().createTypeMapping(true);
                PlexusConfiguration[] types = config.getChild("types").getChildren("type");
                for (int i = 0; i < types.length; i++)
                {
                    initializeType(types[i], tm);
                }

                URL url = null;
                try
                {
                    url = new URL(wsdlUrl);
                }
                catch (MalformedURLException e)
                {
                    url = new File(wsdlUrl).toURL();
                }

                getLogger().info("Creating service with " + url.toString());
                service = builder.create(loadClass(serviceClass), url);
            }
            catch (WSDLException e)
            {
                throw new PlexusConfigurationException("Could not load the WSDL file.", e);
            }
        }
        else
        {
            getLogger().info("Creating service " + name);

            Class clazz = getClass().getClassLoader().loadClass(serviceClass);

            SoapVersion version = null;
            if (soapVersion.equals("1.1"))
            {
                version = Soap11.getInstance();
            }
            else if (soapVersion.equals("1.2"))
            {
                version = Soap12.getInstance();
            }

            service = builder.create(clazz, name, namespace, version, style, use, null);

            PlexusConfiguration[] types = config.getChild("types").getChildren("type");
            for (int i = 0; i < types.length; i++)
            {
                initializeType(types[i],
                               AegisBindingProvider.getTypeMapping(service));
            }
        }

        if (implClass.length() > 0)
        {
            service.setProperty(Service.SERVICE_IMPL_CLASS, loadClass(implClass));
        }

        final String scope = config.getChild("scope").getValue("application");
        if (scope.equals("application"))
            service.setScope(Service.SCOPE_APPLICATION);
        else if (scope.equals("session"))
            service.setScope(Service.SCOPE_SESSION);
        else if (scope.equals("request"))
            service.setScope(Service.SCOPE_REQUEST);

        Invoker invoker = null;

        if (getServiceLocator().hasComponent(serviceClass))
            invoker = new ServiceInvoker(getServiceLocator());
        else
            invoker = new ObjectInvoker();

        service.setInvoker(invoker);

        // Setup pipelines
        service.setRequestPipeline(createHandlerPipeline(config.getChild("requestHandlers")));
        service.setResponsePipeline(createHandlerPipeline(config.getChild("responseHandlers")));
        service.setFaultPipeline(createFaultPipeline(config.getChild("faultHandlers")));

        getServiceRegistry().register(service);

        return service;
    }

    private HandlerPipeline createHandlerPipeline(PlexusConfiguration child)
            throws Exception
    {
        if (child == null)
            return null;

        PlexusConfiguration[] handlers = child.getChildren("handler");
        if (handlers.length == 0)
            return null;

        HandlerPipeline pipe = new HandlerPipeline();

        for (int i = 0; i < handlers.length; i++)
        {
            pipe.addHandler(getHandler(handlers[i].getValue()));
        }

        return pipe;
    }

    private FaultHandlerPipeline createFaultPipeline(PlexusConfiguration child)
            throws Exception
    {
        if (child == null)
            return null;

        PlexusConfiguration[] handlers = child.getChildren("handler");
        if (handlers.length == 0)
            return null;

        FaultHandlerPipeline pipe = new FaultHandlerPipeline();

        for (int i = 0; i < handlers.length; i++)
        {
            pipe.addHandler(getFaultHandler(handlers[i].getValue()));
        }

        return pipe;
    }

    public ServiceFactory getServiceFactory(PlexusConfiguration config)
            throws Exception
    {
        String factoryClass = config.getChild("serviceFactory").getValue("");
        BindingProvider binding = getBindingProvider(config);

        return getServiceFactory(factoryClass, binding);
    }

    protected BindingProvider getBindingProvider(PlexusConfiguration config)
            throws InstantiationException, IllegalAccessException, Exception
    {
        String bindingClass = config.getChild("bindingProvider").getValue("");
        BindingProvider binding = null;
        if (bindingClass.length() > 0)
        {
            binding = (BindingProvider) loadClass(bindingClass).newInstance();
        }
        else
        {
            binding = new AegisBindingProvider(getTypeMappingRegistry());
        }
        return binding;
    }

    /**
     * @return
     * @throws PlexusConfigurationException
     */
    protected ServiceFactory getServiceFactory(String builderClass, BindingProvider bindingProvider)
            throws Exception
    {
        if (builderClass.length() == 0)
        {
            return new ObjectServiceFactory(getXFire().getTransportManager(), null);
        }
        else
        {
            Class clz = loadClass(builderClass);
            Constructor con =
                    clz.getConstructor(new Class[]{TransportManager.class, Class.class});

            return (ServiceFactory)
                    con.newInstance(new Object[]{getXFire().getTransportManager(), bindingProvider});
        }
    }

    private void initializeType(PlexusConfiguration configuration,
                                TypeMapping tm)
            throws Exception
    {
        try
        {
            String ns = configuration.getAttribute("namespace", tm.getEncodingStyleURI());
            String name = configuration.getAttribute("name");

            Type type = (Type) loadClass(configuration.getAttribute("type")).newInstance();

            tm.register(loadClass(configuration.getAttribute("class")),
                        new QName(ns, name),
                        type);
        }
        catch (Exception e)
        {
            if (e instanceof PlexusConfigurationException)
                throw (PlexusConfigurationException) e;

            throw new PlexusConfigurationException("Could not configure type.", e);
        }
    }

    protected Handler getHandler(String name)
            throws Exception
    {
        try
        {
            return (Handler) getServiceLocator().lookup(Handler.ROLE, name);
        }
        catch (ComponentLookupException e)
        {
            return (Handler) loadClass(name).newInstance();
        }
    }

    protected FaultHandler getFaultHandler(String name)
            throws Exception
    {
        try
        {
            return (FaultHandler) getServiceLocator().lookup(FaultHandler.ROLE, name);
        }
        catch (ComponentLookupException e)
        {
            return (FaultHandler) loadClass(name).newInstance();
        }
    }

    public XFire getXFire()
    {
        XFire xfire = null;

        try
        {
            xfire = (XFire) getServiceLocator().lookup(XFire.ROLE);
        }
        catch (ComponentLookupException e)
        {
            throw new RuntimeException("Couldn't find the XFire engine!", e);
        }

        return xfire;
    }

    public TypeMappingRegistry getTypeMappingRegistry()
    {
        TypeMappingRegistry registry = null;

        try
        {
            registry = (TypeMappingRegistry) getServiceLocator().lookup(TypeMappingRegistry.ROLE);
        }
        catch (ComponentLookupException e)
        {
            throw new RuntimeException("Couldn't find the TypeMappingRegistry!", e);
        }

        return registry;
    }

    protected TransportManager getTransportManager()
    {
        TransportManager transMan = null;

        try
        {
            transMan = (TransportManager) getServiceLocator().lookup(TransportManager.ROLE);
        }
        catch (ComponentLookupException e)
        {
            throw new RuntimeException("Couldn't find the TransportManager!", e);
        }

        return transMan;
    }

    /**
     * Load a class from the class loader.
     *
     * @param className The name of the class.
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

        return getClass().getClassLoader().loadClass(className);
    }

    protected ServiceRegistry getServiceRegistry()
    {
        ServiceRegistry registry = null;

        try
        {
            registry = (ServiceRegistry) getServiceLocator().lookup(ServiceRegistry.ROLE);
        }
        catch (ComponentLookupException e)
        {
            throw new RuntimeException("Couldn't find the ServiceRegistry!", e);
        }

        return registry;
    }
}
