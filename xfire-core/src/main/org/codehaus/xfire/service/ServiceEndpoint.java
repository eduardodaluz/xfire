package org.codehaus.xfire.service;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.fault.FaultHandler;
import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.xfire.wsdl11.WSDLCreationVisitorAdapter;

/**
 * Represents a service endpoint. A service endpoints sole job is to process xml messages. The
 * servicehandler is is the central processing point - responsible for invoking 
 * request/response/fault handlers as well reading in the xml message to the service.  
 * <p>
 * The binding is then responsible for taking the SOAP Body and binding it to something - JavaBeans,
 * XMLBeans, W3C DOM tree, etc.
 * <p>
 * The <code>ServiceInfo</code> represents an optional contract for the service. This can be used
 * to generate WSDL and/or provide information on serialization.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @see ServiceInfo
 * @see org.codehaus.xfire.service.binding.SOAPBinding
 */
public class ServiceEndpoint
        implements Visitable
{
    public final static String ROLE = ServiceEndpoint.class.getName();

    private ServiceInfo service;
    private Handler binding;
    private Map properties = new HashMap();
    private WSDLWriter wsdlWriter;
    private FaultHandler faultHandler;
    private Handler serviceHandler;
    private HandlerPipeline requestPipeline;
    private HandlerPipeline responsePipeline;
    private FaultHandlerPipeline faultPipeline;
    private SoapVersion soapVersion;

    /**
     * Initializes a new, default instance of the <code>ServiceEndpoint</code> for a specified 
     * <code>ServiceInfo</code>.
     *
     * @param service the service.
     */
    public ServiceEndpoint(ServiceInfo service)
    {
        this.service = service;
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
    public Handler getBinding()
    {
        return binding;
    }

    /**
     * Sets the binding for this endpoint.
     *
     * @param binding the binding.
     */
    public void setBinding(Handler binding)
    {
        this.binding = binding;
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

    public SoapVersion getSoapVersion()
    {
        return soapVersion;
    }

    public void setSoapVersion(SoapVersion soapVersion)
    {
        this.soapVersion = soapVersion;
    }
}

