package org.codehaus.xfire.jaxws;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.jaxws.binding.AbstractBinding;
import org.codehaus.xfire.jaxws.binding.HTTPBinding;
import org.codehaus.xfire.jaxws.binding.SOAPBinding;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.transport.http.HttpTransport;
import org.codehaus.xfire.transport.http.SoapHttpTransport;

public class JAXWSHelper
{
    private XFire xfire = XFireFactory.newInstance().getXFire();
    private TransportManager tManager = xfire.getTransportManager();
    
    private ServiceFactory serviceFactory = new JAXWSServiceFactory(tManager);
    
    private XFireProxyFactory proxyFactory = new XFireProxyFactory(xfire);
    
    private Map<String, AbstractBinding> bindings = 
        new HashMap<String, AbstractBinding>();
    private Map<Transport, AbstractBinding> transport2Binding = 
        new HashMap<Transport, AbstractBinding>();
    
    private static JAXWSHelper helper = new JAXWSHelper();
    
    protected JAXWSHelper() 
    { 
        Transport soap11 = tManager.getTransport(SoapHttpTransport.SOAP11_HTTP_BINDING);
        AbstractBinding binding = new SOAPBinding(soap11);
        bindings.put(SOAPBinding.SOAP11HTTP_BINDING, binding);
        transport2Binding.put(soap11, binding);
        
        Transport soap12 = tManager.getTransport(SoapHttpTransport.SOAP12_HTTP_BINDING);
        binding = new SOAPBinding(soap12);
        bindings.put(SOAPBinding.SOAP12HTTP_BINDING, binding);
        transport2Binding.put(soap12, binding);
        
        Transport http = tManager.getTransport(HttpTransport.HTTP_BINDING);
        binding = new HTTPBinding(http);
        bindings.put(HTTPBinding.HTTP_BINDING, binding);
        transport2Binding.put(http, binding);
    }
    
    public static JAXWSHelper getInstance()
    {
        return helper;
    }
    
    public ServiceFactory getServiceFactory()
    {
        return serviceFactory;
    }
    
    public XFire getXFire()
    {
        return xfire;
    }
    
    public TransportManager getTransportManager()
    {
        return xfire.getTransportManager();
    }

    public XFireProxyFactory getProxyFactory()
    {
        return proxyFactory;
    }

    public AbstractBinding getBinding(String bindingUri)
    {
        return bindings.get(bindingUri);
    }
    
    public AbstractBinding getBinding(Transport t)
    {
        return transport2Binding.get(t);
    }
}