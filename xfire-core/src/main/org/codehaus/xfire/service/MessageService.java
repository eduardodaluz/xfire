package org.codehaus.xfire.service;

import java.net.URL;
import java.util.Hashtable;

import javax.wsdl.WSDLException;

import org.codehaus.xfire.AbstractXFireComponent;
import org.codehaus.xfire.fault.FaultHandler;
import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.wsdl.ResourceWSDL;
import org.codehaus.xfire.wsdl.WSDLWriter;

/**
 * A simple service implementation.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class MessageService
    extends AbstractXFireComponent
    implements Service
{
    private String name;
        
    private String style;
    
    private String use;
    
    private SoapVersion soapVersion;
    
    private String defaultNamespace;
    
    private Hashtable properties;
    
    private WSDLWriter wsdl;
    
    private FaultHandler faultHandler;
    
    private Handler serviceHandler;
    
    private HandlerPipeline requestPipeline;
    private HandlerPipeline responsePipeline;
    private FaultHandlerPipeline faultPipeline;
    
    public MessageService()
    {
        properties = new Hashtable();   
    }
    
    /**
     * @see org.codehaus.xfire.service.Service#getWSDLWriter()
     */
    public WSDLWriter getWSDLWriter() throws WSDLException
    {
        return wsdl;
    }

    public void setWSDLWriter(WSDLWriter wsdl)
    {
        this.wsdl = wsdl;
    }

    /**
     * @param wsdlUri The WSDL URL.
     */
    public void setWSDLURL(String wsdlUri) throws WSDLException
    {
        if ( wsdlUri == null
             ||
             wsdlUri.equals("") )
        {
            throw new WSDLException(WSDLException.CONFIGURATION_ERROR, "URL to WSDL file is null");
        }
        setWSDLWriter( new ResourceWSDL( wsdlUri ) );
    }

    /**
     * @param wsdlUri The WSDL URL.
     */
    public void setWSDLURL(URL wsdlUri)
    {
        setWSDLWriter( new ResourceWSDL( wsdlUri ) );
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
    public void setDefaultNamespace( String defaultNamespace )
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
    public void setStyle( String style )
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
    public void setUse( String use )
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
    public void setProperty( String name, Object value )
    {
        properties.put( name, value );
    }

    /**
     * @see org.codehaus.xfire.service.Service#getProperty(java.lang.String)
     */
    public Object getProperty( String name )
    {
        return properties.get( name );
    }
    
    public FaultHandler getFaultHandler()
    {
        return faultHandler;
    }
    
    public void setFaultHandler( FaultHandler faultHandler )
    {
        this.faultHandler = faultHandler;
    }
    
    public Handler getServiceHandler()
    {
        return serviceHandler;
    }
    
    public void setServiceHandler( Handler serviceHandler )
    {
        this.serviceHandler = serviceHandler;
    }

    public void setWSDL( WSDLWriter wsdl )
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
}