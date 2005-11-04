package org.codehaus.xfire.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.handler.AbstractHandlerSupport;
import org.codehaus.xfire.service.binding.ObjectBinding;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.wsdl.WSDLWriter;

/**
 * Represents a service endpoint. A service's sole job is to process xml messages. The
 * Binding is is the central processing point.  
 * <p>
 * The binding is then responsible for taking the SOAP Body and binding it to something - JavaBeans,
 * XMLBeans, W3C DOM tree, etc.
 * <p>
 * The <code>ServiceInfo</code> represents all the metadata that goes along with the service.
 * 
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @see ServiceInfo
 * @see org.codehaus.xfire.service.binding.SOAPBinding
 */
public class Service
    extends AbstractHandlerSupport
    implements Visitable
{
    public final static String ROLE = Service.class.getName();

    private ServiceInfo service;
    private MessageSerializer faultSerializer;
    private ObjectBinding binding;
    private Map properties = new HashMap();
    private WSDLWriter wsdlWriter;
    private SoapVersion soapVersion;
    private Map endpoints = new HashMap();
    
    /**
     * Initializes a new, default instance of the <code>ServiceEndpoint</code> for a specified 
     * <code>ServiceInfo</code>.
     *
     * @param service the service.
     */
    public Service(ServiceInfo service)
    {
        this.service = service;
    }


    /**
     * Accepts the given visitor. Iterates over all the contained service.
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
    public ObjectBinding getBinding()
    {
        return binding;
    }

    /**
     * Sets the binding for this endpoint.
     *
     * @param binding the binding.
     */
    public void setBinding(ObjectBinding binding)
    {
        this.binding = binding;
    }

    public MessageSerializer getFaultSerializer()
    {
        return faultSerializer;
    }

    public void setFaultSerializer(MessageSerializer faultSerializer)
    {
        this.faultSerializer = faultSerializer;
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
     * Returns the service descriptor for this endpoint.
     *
     * @return the service descriptor.
     */
    public ServiceInfo getServiceInfo()
    {
        return service;
    }

    /**
     * Returns the <code>WSDLWriter</code> for this endpoint. If a writer has not been {@link #setWSDLWriter(WSDLWriter)
     * explicitly set}, a default implementation is used.
     *
     * @return the wsdl writer.
     */
    public WSDLWriter getWSDLWriter()
    {
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
    
    public Collection getEndpoints()
    {
        return Collections.unmodifiableCollection(endpoints.values());
    }
    
    public void addEndpoint(Endpoint endpoint)
    {
        endpoints.put(endpoint.getName(), endpoint);
    }
    
    public Endpoint getEndpoint(QName name)
    {
        return (Endpoint) endpoints.get(name);
    }
    
}

