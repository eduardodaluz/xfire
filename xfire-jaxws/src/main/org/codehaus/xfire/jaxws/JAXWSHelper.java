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

class JAXWSHelper
{
    private XFire xfire = XFireFactory.newInstance().getXFire();
    private TransportManager tManager = xfire.getTransportManager();
    
    private ServiceFactory serviceFactory = new JAXWSServiceFactory(tManager);
    
    private XFireProxyFactory proxyFactory = new XFireProxyFactory(xfire);
    
    private Map<String, AbstractBinding> bindings = 
        new HashMap<String, AbstractBinding>();
    
    private static JAXWSHelper helper = new JAXWSHelper();
    
    protected JAXWSHelper() 
    { 
        Transport soap11 = tManager.getTransport(SoapHttpTransport.SOAP11_HTTP_BINDING);
        bindings.put(SOAPBinding.SOAP11HTTP_BINDING, new SOAPBinding(soap11));
        
        Transport soap12 = tManager.getTransport(SoapHttpTransport.SOAP12_HTTP_BINDING);
        bindings.put(SOAPBinding.SOAP12HTTP_BINDING, new SOAPBinding(soap12));
        
        Transport http = tManager.getTransport(HttpTransport.HTTP_BINDING);
        bindings.put(HTTPBinding.HTTP_BINDING, new HTTPBinding(http));
    }
    
    static JAXWSHelper getInstance()
    {
        return helper;
    }
    
    ServiceFactory getServiceFactory()
    {
        return serviceFactory;
    }
    
    XFire getXFire()
    {
        return xfire;
    }
    
    TransportManager getTransportManager()
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
}