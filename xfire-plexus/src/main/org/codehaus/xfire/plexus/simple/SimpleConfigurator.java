package org.codehaus.xfire.plexus.simple;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.xfire.SOAPConstants;
import org.codehaus.xfire.fault.SOAP11FaultHandler;
import org.codehaus.xfire.fault.SOAP12FaultHandler;
import org.codehaus.xfire.handler.SoapHandler;
import org.codehaus.xfire.java.JavaServiceHandler;
import org.codehaus.xfire.plexus.PlexusXFireComponent;
import org.codehaus.xfire.plexus.ServiceInvoker;
import org.codehaus.xfire.plexus.config.Configurator;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.SimpleService;

/**
 * Creates and configures SimpleServices.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 20, 2004
 */
public class SimpleConfigurator 
    extends PlexusXFireComponent
    implements Configurator
{
    final public static String SERVICE_TYPE = "simple";
    
    /**
     * @see org.codehaus.xfire.plexus.config.Configurator#getServiceType()
     */
    public String getServiceType()
    {
        return SERVICE_TYPE;
    }
    
    public Service createService( PlexusConfiguration config ) 
        throws Exception
    {
        SimpleService s = new SimpleService();
        
        configureService(config, s);

        getServiceRegistry().register(s);
		
        return s;
    }
    
    protected void configureService(PlexusConfiguration config, SimpleService s)
        throws PlexusConfigurationException
    {
        ServiceInvoker invoker = new ServiceInvoker(getServiceLocator());
        JavaServiceHandler handler = new JavaServiceHandler(invoker);
        SoapHandler sHandler = new SoapHandler(handler);
        s.setServiceHandler(sHandler);
        
        s.setName( config.getChild("name").getValue() );
        
        s.setDefaultNamespace( config.getChild( "namespace" ).getValue("") );
    
        s.setWSDLURL( config.getChild("wsdlURL").getValue() );
        
        s.setUse( config.getChild("use").getValue("literal") );
        
        s.setStyle( config.getChild("style").getValue("wrapped") );

        String soapNS = config.getChild( "soapVersion" ).getValue("1.1");
        
        if ( soapNS.equals("1.1") )
        {
            s.setSoapVersion( SOAPConstants.SOAP11_ENVELOPE_NS );
            s.setFaultHandler( new SOAP11FaultHandler() );
        }
        else if ( soapNS.equals("1.2") )
        {
            s.setSoapVersion( SOAPConstants.SOAP12_ENVELOPE_NS );
            s.setFaultHandler( new SOAP12FaultHandler() );
        }
        else
            throw new PlexusConfigurationException("Invalid soap version.  Must be 1.1 or 1.2.");
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
}
