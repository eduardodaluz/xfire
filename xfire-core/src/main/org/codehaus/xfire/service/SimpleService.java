package org.codehaus.xfire.service;

import java.util.Hashtable;

import javax.wsdl.WSDLException;

import org.codehaus.xfire.AbstractXFireComponent;
import org.codehaus.xfire.fault.FaultHandler;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.wsdl.ResourceWSDL;
import org.codehaus.xfire.wsdl.WSDLWriter;

/**
 * A simple service implementation.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SimpleService
    extends AbstractXFireComponent
    implements Service
{
    private String name;
        
    private String style;
    
    private String use;
    
    private String soapVersion;
    
    private String defaultNamespace;
    
    private Hashtable properties;
    
    private WSDLWriter wsdl;
    
    private String wsdlUri;
 
    private FaultHandler faultHandler;
    
    private Handler serviceHandler;
    
    public SimpleService()
    {
        properties = new Hashtable();   
    }
    
    /**
     * @see org.codehaus.xfire.service.Service#getWSDLWriter()
     */
    public WSDLWriter getWSDLWriter() throws WSDLException
    {
        if ( wsdl == null )
        {
            if ( getWSDLURL() != null 
                 &&
                 !getWSDLURL().equals("") )
            {
                wsdl = new ResourceWSDL( wsdlUri );
            }
        }
        
        return wsdl;
    }

    
    
    /**
     * @return Returns the URL to the WSDL for this service. 
     * If none exists, the service will attempt to generage
     * the WSDL automatically via the WSDLBuilder.
     */
    public String getWSDLURL()
    {
        return wsdlUri;
    }
    
    /**
     * @param wsdlUri The WSDL URL.
     */
    public void setWSDLURL(String wsdlUri)
    {
        this.wsdlUri = wsdlUri;
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
    public String getSoapVersion()
    {
        return soapVersion;
    }

	public void setSoapVersion(String soapVersion)
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
     * @see org.codehaus.xfire.service.ServiceDescriptor#setProperty(java.lang.String, java.lang.Object)
     */
    public void setProperty( String name, Object value )
    {
        properties.put( name, value );
    }

    /**
     * @see org.codehaus.xfire.service.ServiceDescriptor#getProperty(java.lang.String)
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

    public HandlerPipeline getRequestPipeline()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public HandlerPipeline getResponsePipeline()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public HandlerPipeline getFaultPipeline()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
