package org.codehaus.xfire.plexus;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Configurable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceLocator;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Serviceable;
import org.codehaus.xfire.SOAPConstants;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.FaultHandler;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.SimpleService;

/**
 * MockService
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PlexusService
	extends SimpleService
	implements Configurable, Initializable, Serviceable
{
    private String serviceHandlerHint;
    
    private String faultHandlerHint;
    
    private ServiceLocator manager;
    
    private Handler serviceHandler;
    
    /**
     * Registers this service with the service registry.
     * 
     * @see org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        getServiceRegistry().register( this );
    }
    
	/**
	 * @see org.codehaus.xfire.service.Service#getServiceProvider()
	 */
	public Handler getServiceHandler()
    {
        try
        {
            return (Handler) getServiceLocator().lookup( Handler.ROLE, serviceHandlerHint );
        }
        catch (ComponentLookupException e)
        {
            throw new XFireRuntimeException( "Couldn't find service provider!", e );
        }
    }

    public FaultHandler getFaultHandler()
    {
        try
        {
            return (FaultHandler) getServiceLocator().lookup( FaultHandler.ROLE, faultHandlerHint );
        }
        catch (ComponentLookupException e)
        {
            throw new XFireRuntimeException( "Couldn't find service provider!", e );
        }
    }
    
    public String getFaultHandlerHint()
    {
        return faultHandlerHint;
    }
    
    public void setFaultHandlerHint(String faultHandlerHint)
    {
        this.faultHandlerHint = faultHandlerHint;
    }
    
    public String getProviderHint()
    {
        return serviceHandlerHint;
    }
    
    public void setProviderHint(String serviceHandlerHint)
    {
        this.serviceHandlerHint = serviceHandlerHint;
    }
    
    public void configure(PlexusConfiguration config) 
        throws PlexusConfigurationException
    {
        setName( config.getChild("name").getValue() );
        
        setDefaultNamespace( config.getChild( "namespace" ).getValue("") );
    
        setWsdlUri( config.getChild("wsdlUri").getValue("") );
        
        setUse( config.getChild("use").getValue("literal") );
        
        setStyle( config.getChild("style").getValue("wrapped") );

        String soapNS = config.getChild( "soapVersion" ).getValue("1.1");
        
        if ( soapNS.equals("1.1") )
            setSoapVersion( SOAPConstants.SOAP11_ENVELOPE_NS );
        else if ( soapNS.equals("1.2") )
            setSoapVersion( SOAPConstants.SOAP12_ENVELOPE_NS );
        else
            throw new PlexusConfigurationException("Invalid soap version.  Must be 1.1 or 1.2.");
        
        setFaultHandlerHint( config.getChild( "faultHandler" ).getValue() );
        
        setProviderHint( config.getChild( "serviceHandler" ).getValue() );
    }
    
    protected ServiceRegistry getServiceRegistry()
    {
        ServiceRegistry registry = null;
        
        try
        {
            registry = (ServiceRegistry) getServiceLocator().lookup( ServiceRegistry.ROLE );
        }
        catch (ComponentLookupException e)
        {
            throw new RuntimeException( "Couldn't find the ServiceRegistry!", e );
        }
        
        return registry;
    }
    
    public void service( ServiceLocator manager )
    {
        this.manager = manager;
    }
    
    /**
     * @return Returns the service manager.
     */
    protected ServiceLocator getServiceLocator()
    {
        return manager;
    }
}
