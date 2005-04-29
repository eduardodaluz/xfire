package org.codehaus.xfire.service;

import java.net.URL;
import java.util.Collection;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.codehaus.xfire.fault.FaultHandler;
import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.Invoker;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.wsdl.WSDLWriter;

/**
 * Temporary adapter that wraps a <code>ServiceEndpoint</code> so that it follows the contract of the
 * <code>Service</code> interface.
 *
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
        return endpoint.getBinding().getStyle();
    }

    public String getUse()
    {
        return endpoint.getBinding().getUse();
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
        return endpoint.getBinding().getSoapVersion();
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

    public void setWSDLURL(URL wsdlUri)
    {
        endpoint.setWSDLURL(wsdlUri);
    }

    public void setSoapVersion(SoapVersion soapVersion)
    {
        endpoint.getBinding().setSoapVersion(soapVersion);
    }

    public void setStyle(String style)
    {
        endpoint.getBinding().setStyle(style);
    }

    public void setUse(String use)
    {
        endpoint.getBinding().setUse(use);
    }

    public void setName(String name)
    {
        endpoint.getService().setName(new QName(name));
    }

    public void setFaultHandler(FaultHandler faultHandler)
    {
        endpoint.setFaultHandler(faultHandler);
    }

    public void setServiceHandler(Handler serviceHandler)
    {
        endpoint.setServiceHandler(serviceHandler);
    }

    public void setWSDLURL(String wsdlUri)
            throws WSDLException
    {
        endpoint.setWSDLURL(wsdlUri);
    }

    public void setScope(int scope)
    {
        endpoint.setScope(scope);
    }

    public void setFaultPipeline(FaultHandlerPipeline faultPipeline)
    {
        endpoint.setFaultPipeline(faultPipeline);
    }

    public void setRequestPipeline(HandlerPipeline requestPipeline)
    {
        endpoint.setRequestPipeline(requestPipeline);
    }

    public void setResponsePipeline(HandlerPipeline responsePipeline)
    {
        endpoint.setResponsePipeline(responsePipeline);
    }
}
