package org.codehaus.xfire.service;

import java.util.Collection;
import javax.wsdl.WSDLException;

import org.codehaus.xfire.fault.FaultHandler;
import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.Invoker;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.wsdl.WSDLWriter;

/**
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class ServiceEndpointAdapter
        implements Service
{
    private ServiceEndpoint endpoint;

    public ServiceEndpointAdapter(ServiceEndpoint endpoint)
    {
        this.endpoint = endpoint;
    }

    public WSDLWriter getWSDLWriter()
            throws WSDLException
    {
        return endpoint.getWSDLWriter();
    }

    public Handler getServiceHandler()
    {
        return endpoint.getServiceHandler();
    }

    public HandlerPipeline getRequestPipeline()
    {
        return endpoint.getRequestPipeline();
    }

    public HandlerPipeline getResponsePipeline()
    {
        return endpoint.getResponsePipeline();
    }

    public FaultHandlerPipeline getFaultPipeline()
    {
        return endpoint.getFaultPipeline();
    }

    public FaultHandler getFaultHandler()
    {
        return endpoint.getFaultHandler();
    }

    public String getStyle()
    {
        return endpoint.getStyle();
    }

    public String getUse()
    {
        return endpoint.getUse();
    }

    public String getName()
    {
        return endpoint.getService().getName().getLocalPart();
    }

    public String getDefaultNamespace()
    {
        return endpoint.getService().getName().getNamespaceURI();
    }

    public void setProperty(String name, Object value)
    {
        endpoint.setProperty(name, value);
    }

    public Object getProperty(String name)
    {
        return endpoint.getProperty(name);
    }

    public SoapVersion getSoapVersion()
    {
        return endpoint.getSoapVersion();
    }

    public BindingProvider getBindingProvider()
    {
        return endpoint.getBindingProvider();
    }

    public Invoker getInvoker()
    {
        return endpoint.getInvoker();
    }

    public OperationInfo getOperation(String name)
    {
        return endpoint.getService().getOperation(name);
    }

    public void removeOperation(String string)
    {
        endpoint.getService().removeOperation(string);
    }

    public void addOperation(OperationInfo info)
    {
        endpoint.getService().addOperation(info);
    }

    public Collection getOperations()
    {
        return endpoint.getService().getOperations();
    }

    public int getScope()
    {
        return endpoint.getScope();
    }

    public Class getServiceClass()
    {
        return endpoint.getService().getServiceClass();
    }

    public void setInvoker(Invoker invoker)
    {
        endpoint.setInvoker(invoker);
    }
}
