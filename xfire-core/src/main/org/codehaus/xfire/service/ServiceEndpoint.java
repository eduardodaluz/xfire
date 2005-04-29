package org.codehaus.xfire.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.wsdl.WSDLException;

import org.codehaus.xfire.fault.FaultHandler;
import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.Invoker;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.wsdl.ResourceWSDL;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;

/**
 * Represents a service endpoint. A service endpoint is a resource to which web service messages can be addressed.
 * Endpoint references convey the information needed to address a web service endpoint.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class ServiceEndpoint
        implements Visitable
{
    private ServiceInfo service;
    private String style;
    private String use;
    private SoapVersion soapVersion;
    private Map properties = new HashMap();
    private WSDLWriter wsdlWriter;
    private FaultHandler faultHandler;
    private Handler serviceHandler;
    private HandlerPipeline requestPipeline;
    private HandlerPipeline responsePipeline;
    private FaultHandlerPipeline faultPipeline;
    private BindingProvider bindingProvider;
    private List allowedMethods = new ArrayList();
    private WSDLBuilder wsdlBuilder;
    private int scope = Service.SCOPE_APPLICATION;
    private Invoker invoker;

    /**
     * Initializes a new instance of the <code>ServiceEndpoint</code> for a specified <code>ServiceInfo</code>.
     *
     * @param service the service.
     */
    public ServiceEndpoint(ServiceInfo service)
    {
        this.service = service;
    }

    /**
     * Acceps the given visitor. Iterates over all operation infos.
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

    public WSDLWriter getWSDLWriter()
            throws WSDLException
    {
        if (wsdlWriter == null)
        {
            final WSDLBuilder builder = getWSDLBuilder();

            if (builder != null)
                return getWSDLBuilder().createWSDLWriter(new ServiceEndpointAdapter(this));
        }

        return wsdlWriter;
    }

    public WSDLBuilder getWSDLBuilder()
    {
        return wsdlBuilder;
    }

    /**
     * Returns the WSDL writer for this service endpoint.
     *
     * @return the WSDL writer.
     */
    public WSDLWriter getWsdlWriter()
    {
        return null;
    }

    /**
     * Load a class from the class loader.
     *
     * @param className The name of the class.
     * @return The class.
     */
    protected Class loadClass(final String className)
            throws ClassNotFoundException
    {
        try
        {
            return getClass().getClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException cnfe)
        {
            try
            {
                return Class.forName(className);
            }
            catch (ClassNotFoundException cnf2)
            {
                return Thread.currentThread().getContextClassLoader().loadClass(className);
            }
        }
    }

    /**
     * @see org.codehaus.xfire.service.Service#setProperty(java.lang.String, java.lang.Object)
     */
    public void setProperty(String name, Object value)
    {
        properties.put(name, value);
    }

    public void setWSDL(WSDLWriter wsdlWriter)
    {
        this.wsdlWriter = wsdlWriter;
    }

    public void setWSDLBuilder(final WSDLBuilder wsdlBuilder)
    {
        this.wsdlBuilder = wsdlBuilder;
    }

    /**
     * @param wsdlUri The WSDL URL.
     */
    public void setWSDLURL(URL wsdlUri)
    {
        setWSDLWriter(new ResourceWSDL(wsdlUri));
    }

    /**
     * @param wsdlUri The WSDL URL.
     */
    public void setWSDLURL(String wsdlUri)
            throws WSDLException
    {
        if (wsdlUri == null
                ||
                wsdlUri.equals(""))
        {
            throw new WSDLException(WSDLException.CONFIGURATION_ERROR, "URL to WSDL file is null");
        }
        setWSDLWriter(new ResourceWSDL(wsdlUri));
    }

    public void setWSDLWriter(WSDLWriter wsdl)
    {
        this.wsdlWriter = wsdl;
    }

    public List getAllowedMethods()
    {
        return allowedMethods;
    }

    /**
     * @param allowedMethods The allowedMethods to set.
     */
    public void setAllowedMethods(final List allowedMethods)
    {
        this.allowedMethods = allowedMethods;
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

    public SoapVersion getSoapVersion()
    {
        return soapVersion;
    }

    public String getStyle()
    {
        return style;
    }

    public String getUse()
    {
        return use;
    }
}

