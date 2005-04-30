package org.codehaus.xfire.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class DefaultService
        implements Service
{
    private String name;

    private String style;

    private String use;

    private SoapVersion soapVersion;

    private String defaultNamespace;

    private Map properties;

    private WSDLWriter wsdl;

    private FaultHandler faultHandler;

    private Handler serviceHandler;

    private HandlerPipeline requestPipeline;
    private HandlerPipeline responsePipeline;
    private FaultHandlerPipeline faultPipeline;

    private BindingProvider bindingProvider;

    private List allowedMethods;

    private Class serviceClass;

    private Map operations;

    private WSDLBuilder wsdlBuilder;

    private boolean autoTyped = false;

    private int scope = Service.SCOPE_APPLICATION;

    private String encodingStyleURI;

    private Invoker invoker;

    public DefaultService()
    {
        super();
        this.allowedMethods = new ArrayList();
        this.operations = new HashMap();
        this.properties = new HashMap();
    }

    public void setWSDLWriter(WSDLWriter wsdl)
    {
        this.wsdl = wsdl;
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

    /**
     * @param wsdlUri The WSDL URL.
     */
    public void setWSDLURL(URL wsdlUri)
    {
        setWSDLWriter(new ResourceWSDL(wsdlUri));
    }

    /**
     * @return Returns the defaultNamespace.
     */
    public String getDefaultNamespace()
    {
        return defaultNamespace;
    }

    /**
     * @param defaultNamespace The defaultNamespace to set.
     */
    public void setDefaultNamespace(String defaultNamespace)
    {
        this.defaultNamespace = defaultNamespace;
    }

    /**
     * @return Returns the style.
     */
    public String getStyle()
    {
        return style;
    }

    /**
     * @param style The style to set.
     */
    public void setStyle(String style)
    {
        this.style = style;
    }

    /**
     * @return Returns the use.
     */
    public String getUse()
    {
        return use;
    }

    /**
     * @param use The use to set.
     */
    public void setUse(String use)
    {
        this.use = use;
    }

    /**
     * @see org.codehaus.xfire.service.Service#getSoapVersion()
     */
    public SoapVersion getSoapVersion()
    {
        return soapVersion;
    }

    public void setSoapVersion(SoapVersion soapVersion)
    {
        this.soapVersion = soapVersion;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @see org.codehaus.xfire.service.Service#setProperty(java.lang.String, java.lang.Object)
     */
    public void setProperty(String name, Object value)
    {
        properties.put(name, value);
    }

    /**
     * @see org.codehaus.xfire.service.Service#getProperty(java.lang.String)
     */
    public Object getProperty(String name)
    {
        return properties.get(name);
    }

    public FaultHandler getFaultHandler()
    {
        return faultHandler;
    }

    public void setFaultHandler(FaultHandler faultHandler)
    {
        this.faultHandler = faultHandler;
    }

    public Handler getServiceHandler()
    {
        return serviceHandler;
    }

    public void setServiceHandler(Handler serviceHandler)
    {
        this.serviceHandler = serviceHandler;
    }

    public void setWSDL(WSDLWriter wsdl)
    {
        this.wsdl = wsdl;
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

    public BindingProvider getBindingProvider()
    {
        return bindingProvider;
    }

    public void setBindingProvider(BindingProvider bindingProvider)
    {
        this.bindingProvider = bindingProvider;
    }

    /**
     * @param className
     */
    public void setServiceClass(final String className)
            throws ClassNotFoundException
    {
        setServiceClass(loadClass(className));
    }

    /**
     * @param serviceClass
     */
    public void setServiceClass(final Class serviceClass)
    {
        this.serviceClass = serviceClass;
    }

    public void addOperation(final OperationInfo op)
    {
        operations.put(op.getName(), op);
    }

    public OperationInfo getOperation(final String localName)
    {
        return (OperationInfo) operations.get(localName);
    }

    public Collection getOperations()
    {
        return operations.values();
    }

    protected Map getOperationsMap()
    {
        return operations;
    }

    public void removeOperation(String name)
    {
        operations.remove(name);
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

    /**
     * @return
     */
    public Class getServiceClass()
    {
        return serviceClass;
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

    public WSDLWriter getWSDLWriter()
            throws WSDLException
    {
        if (wsdl == null)
        {
            final WSDLBuilder b = getWSDLBuilder();

            if (b != null)
                return getWSDLBuilder().createWSDLWriter(this);
        }

        return wsdl;
    }

    public int getScope()
    {
        return scope;
    }

    public void setScope(final int scope)
    {
        this.scope = scope;
    }

    public WSDLBuilder getWSDLBuilder()
    {
        return wsdlBuilder;
    }

    public void setWSDLBuilder(final WSDLBuilder wsdlBuilder)
    {
        this.wsdlBuilder = wsdlBuilder;
    }

    public Invoker getInvoker()
    {
        return invoker;
    }

    public void setInvoker(Invoker invoker)
    {
        this.invoker = invoker;
    }
}
