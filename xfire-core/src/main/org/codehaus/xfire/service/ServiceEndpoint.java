package org.codehaus.xfire.service;

import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

import org.codehaus.xfire.fault.FaultHandler;
import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.service.binding.Binding;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.Invoker;
import org.codehaus.xfire.service.binding.SOAPBindingFactory;
import org.codehaus.xfire.service.transport.Transport;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.xfire.wsdl11.WSDLCreationVisitorAdapter;

/**
 * Represents a service endpoint. A service endpoint is a resource to which web service messages can be addressed.
 * Endpoint references convey the information needed to address a web service endpoint.
 * <p/>
 * The <code>ServiceEndpoint</code> is basically a facade for a <code>ServiceInfo</code>, <code>Binding</code>. As such,
 * it provides a unified interface to these subsystems.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @see ServiceInfo
 * @see Binding
 * @see org.codehaus.xfire.service.binding.SOAPBinding
 * @see Transport
 */
public class ServiceEndpoint
        implements Visitable
{
    private ServiceInfo service;
    private Binding binding;
    private Map properties = new HashMap();
    private WSDLWriter wsdlWriter;
    private FaultHandler faultHandler;
    private Handler serviceHandler;
    private HandlerPipeline requestPipeline;
    private HandlerPipeline responsePipeline;
    private FaultHandlerPipeline faultPipeline;
    private BindingProvider bindingProvider;
    private int scope = Service.SCOPE_APPLICATION;
    private Invoker invoker;

    /**
     * Initializes a new, default instance of the <code>ServiceEndpoint</code> for a specified <code>ServiceInfo</code>.
     * It uses a <code>SOAPBinding</code> and a <code>HTTPTransport</code>.
     *
     * @param service the service.
     */
    public ServiceEndpoint(ServiceInfo service)
    {
        this(service, null);
    }

    /**
     * Initializes a new instance of the <code>ServiceEndpoint</code> for a specified <code>ServiceInfo</code>,
     * <code>Binding</code> and <code>Transport</code>.
     *
     * @param service the service.
     * @param binding the binding.
     */
    public ServiceEndpoint(ServiceInfo service, Binding binding)
    {
        this.service = service;
        if (binding != null)
        {
            this.binding = binding;
        }
        else
        {
            this.binding = SOAPBindingFactory.createSOAPBinding(
                    new QName(service.getName().getLocalPart() + "Binding"),
                    SoapConstants.STYLE_DOCUMENT,
                    SoapConstants.USE_LITERAL);
        }
    }

    /**
     * Acceps the given visitor. Iterates over all the contained service.
     *
     * @param visitor the visitor.
     */
    public void accept(Visitor visitor)
    {
        visitor.startEndpoint(this);
        service.accept(visitor);
        visitor.endEndpoint(this);
    }

    /**
     * @see org.codehaus.xfire.service.Service#getProperty(java.lang.String)
     */
    public Object getProperty(String name)
    {
        return properties.get(name);
    }

    /**
     * @see org.codehaus.xfire.service.Service#setProperty(java.lang.String, java.lang.Object)
     */
    public void setProperty(String name, Object value)
    {
        properties.put(name, value);
    }

    /**
     * Returns the binding for this endpoint.
     *
     * @return the binding.
     */
    public Binding getBinding()
    {
        return binding;
    }

    /**
     * Sets the binding for this endpoint.
     *
     * @param binding the binding.
     */
    public void setBinding(Binding binding)
    {
        this.binding = binding;
    }

    public BindingProvider getBindingProvider()
    {
        return bindingProvider;
    }

    public void setBindingProvider(BindingProvider bindingProvider)
    {
        this.bindingProvider = bindingProvider;
    }

    public FaultHandler getFaultHandler()
    {
        return faultHandler;
    }

    public void setFaultHandler(FaultHandler faultHandler)
    {
        this.faultHandler = faultHandler;
    }

    /**
     * @return Returns the faultPipeline.
     */
    public FaultHandlerPipeline getFaultPipeline()
    {
        return faultPipeline;
    }

    /**
     * @param faultPipeline The faultPipeline to set.
     */
    public void setFaultPipeline(FaultHandlerPipeline faultPipeline)
    {
        this.faultPipeline = faultPipeline;
    }

    public Invoker getInvoker()
    {
        return invoker;
    }

    public void setInvoker(Invoker invoker)
    {
        this.invoker = invoker;
    }

    /**
     * Returns the name of this endpoint. This method simply returns the local part of the qualified name of the
     * <code>ServiceInfo</code>.
     *
     * @return the service name.
     * @see ServiceInfo#getName()
     * @see javax.xml.namespace.QName#getLocalPart()
     */
    public String getName()
    {
        return service.getName().getLocalPart();
    }

    /**
     * @return Returns the requestPipeline.
     */
    public HandlerPipeline getRequestPipeline()
    {
        return requestPipeline;
    }

    /**
     * @param requestPipeline The requestPipeline to set.
     */
    public void setRequestPipeline(HandlerPipeline requestPipeline)
    {
        this.requestPipeline = requestPipeline;
    }

    /**
     * @return Returns the responsePipeline.
     */
    public HandlerPipeline getResponsePipeline()
    {
        return responsePipeline;
    }

    /**
     * @param responsePipeline The responsePipeline to set.
     */
    public void setResponsePipeline(HandlerPipeline responsePipeline)
    {
        this.responsePipeline = responsePipeline;
    }

    public int getScope()
    {
        return scope;
    }

    public void setScope(final int scope)
    {
        this.scope = scope;
    }

    /**
     * Returns the service descriptor for this endpoint.
     *
     * @return the service descriptor.
     */
    public ServiceInfo getService()
    {
        return service;
    }

    public Handler getServiceHandler()
    {
        return serviceHandler;
    }

    public void setServiceHandler(Handler serviceHandler)
    {
        this.serviceHandler = serviceHandler;
    }

    /**
     * Returns the <code>WSDLWriter</code> for this endpoint. If a writer has not been {@link #setWSDLWriter(WSDLWriter)
     * explicitly set}, a default implementation is used.
     *
     * @return the wsdl writer.
     */
    public WSDLWriter getWSDLWriter()
    {
        if (wsdlWriter == null)
        {
            wsdlWriter = new WSDLCreationVisitorAdapter(this);
        }
        return wsdlWriter;
    }

    /**
     * Sets the <code>WSDLWriter</code> for this endpoint.
     *
     * @param wsdlWriter
     */
    public void setWSDLWriter(WSDLWriter wsdlWriter)
    {
        this.wsdlWriter = wsdlWriter;
    }
}

