package org.codehaus.xfire.jaxws;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Dispatch;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.handler.HandlerResolver;

import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.jaxws.handler.SimpleHandlerResolver;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.transport.Transport;

public class ServiceDelegate
    extends javax.xml.ws.spi.ServiceDelegate
{
    private JAXWSHelper jaxWsHelper = JAXWSHelper.getInstance();
    private Service service;
    private XFireProxyFactory factory = jaxWsHelper.getProxyFactory();
    private ServiceFactory serviceFactory = jaxWsHelper.getServiceFactory();

    private URL wsdlDocumentLocation;
    private Executor executor;
    private HandlerResolver handlerResolver;
    private Class serviceClass;
    private QName serviceName;

    public ServiceDelegate()
    {
        handlerResolver = new SimpleHandlerResolver();
    }
    
    public ServiceDelegate(URL wsdlLocation, QName serviceName, Class clientClass)
    {
        this();
        
        this.wsdlDocumentLocation = wsdlLocation;
        this.serviceName = serviceName;

        try
        {
            this.serviceClass = (Class) clientClass.getField("SERVICE_CLASS").get(null);
        }
        catch (Exception e)
        {
            throw new WebServiceException("Could not find service class on " + clientClass.toString());
        }
        
        this.service = serviceFactory.create(serviceClass, wsdlLocation, null);
        
        WebServiceClient wsClient = (WebServiceClient) clientClass.getAnnotation(WebServiceClient.class);
        
        Method[] methods = clientClass.getMethods();
        for(Method m : methods)
        {
            if (m.isAnnotationPresent(WebEndpoint.class))
            {
                WebEndpoint we = m.getAnnotation(WebEndpoint.class);
                
                // TODO             
            }
        }
    }

    @Override
    public <T> T getPort(QName portName, Class<T> clazz)
    {
        Endpoint endpoint = service.getEndpoint(portName);
        
        if (endpoint == null) throw new WebServiceException("Invalid port name " + portName);
        
        return (T) createPort(endpoint);
    }

    private Object createPort(Endpoint endpoint)
    {
        try
        {
            return factory.create(endpoint);
        }
        catch (MalformedURLException e)
        {
            throw new WebServiceException("Invalid url: " + endpoint.getAddress(), e);
        }
    }

    @Override
    public <T> T getPort(Class<T> arg0)
    {
        if (service.getEndpoints().size() == 0)
        {
            throw new WebServiceException("No available ports.");
        }
        
        return (T) createPort((Endpoint) service.getEndpoints().iterator().next());
    }

    @Override
    public void addPort(QName portName, URI bindingUri, String address)
    {
        Binding binding = getOrCreateBinding(bindingUri);
        Endpoint endpoint = new Endpoint(portName, binding, address);
        
        service.addEndpoint(endpoint);
    }

    protected Binding getOrCreateBinding(URI bindingUri)
    {
        Transport t = jaxWsHelper.getBinding(bindingUri.toString()).getTransport();
        
        if (t == null)
        {
            throw new WebServiceException("Could not find a transport for binding " + bindingUri);
        }
        
        Binding b = service.getBinding(t);
        
        return b;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> javax.xml.ws.Dispatch<T> createDispatch(QName port, Class<T> type, Mode serviceMode)
    {
        if (type == Source.class)
        {
            return (Dispatch<T>) new SourceDispatch();
        }
        
        return null;
    }

    @Override
    public javax.xml.ws.Dispatch<Object> createDispatch(QName arg0, JAXBContext arg1, Mode arg2)
    {
        return null;
    }

    @Override
    public QName getServiceName()
    {
        return serviceName;
    }

    @Override
    public Iterator<QName> getPorts()
    {
        List<QName> ports = new ArrayList<QName>();
        
        for (Iterator itr = service.getEndpoints().iterator(); itr.hasNext();)
        {
            ports.add(((Endpoint) itr.next()).getName());
        }
        
        return ports.iterator();
    }

    @Override
    public URL getWSDLDocumentLocation()
    {
        return wsdlDocumentLocation;
    }

    @Override
    public HandlerResolver getHandlerResolver()
    {
        return handlerResolver;
    }

    @Override
    public void setHandlerResolver(HandlerResolver handlerResolver)
    {
        this.handlerResolver = handlerResolver;
    }

    @Override
    public Executor getExecutor()
    {
        return executor;
    }

    @Override
    public void setExecutor(Executor executor)
    {
        this.executor = executor;
    }
}
