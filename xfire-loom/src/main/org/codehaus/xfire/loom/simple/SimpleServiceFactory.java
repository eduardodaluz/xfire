package org.codehaus.xfire.loom.simple;

import javax.wsdl.WSDLException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.codehaus.xfire.fault.Soap11FaultHandler;
import org.codehaus.xfire.fault.Soap12FaultHandler;
import org.codehaus.xfire.handler.SoapHandler;
import org.codehaus.xfire.java.JavaServiceHandler;
import org.codehaus.xfire.loom.ServiceFactory;
import org.codehaus.xfire.loom.ServiceInvoker;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.SimpleService;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;

/**
 * Creates and configures SimpleServices.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class SimpleServiceFactory implements ServiceFactory
{
    public String getType()
    {
        return "simple";
    }

    public Service createService( final Object target, final Configuration configuration )
        throws Exception
    {
        final SimpleService s = new SimpleService();

        configureService( configuration, s, target );

        return s;
    }

    protected void configureService( final Configuration configuration,
                                     final SimpleService service,
                                     final Object target )
        throws ConfigurationException
    {
        service.setServiceHandler( new SoapHandler( new JavaServiceHandler( new ServiceInvoker( target ) ) ) );

        service.setName( configuration.getChild( "name" ).getValue() );
        service.setDefaultNamespace( configuration.getChild( "namespace" ).getValue( "" ) );

        try
        {
            final String wsdlUrl = configuration.getChild( "wsdlURL" ).getValue( "" );

            if( !wsdlUrl.equals( "" ) )
            {
                service.setWSDLURL( wsdlUrl );
            }
        }
        catch( WSDLException e )
        {
            final String msg = "Problems with WSDL URL at " + configuration.getChild( "wsdlURL" ).getLocation();
            throw new ConfigurationException( msg, e );
        }

        service.setUse( configuration.getChild( "use" ).getValue( "literal" ) );

        service.setStyle( configuration.getChild( "style" ).getValue( "wrapped" ) );

        final String soapNS = configuration.getChild( "soapVersion" ).getValue( "1.1" );

        if( soapNS.equals( "1.1" ) )
        {
            service.setSoapVersion( Soap11.getInstance() );
            service.setFaultHandler( new Soap11FaultHandler() );
        }
        else if( soapNS.equals( "1.2" ) )
        {
            service.setSoapVersion( Soap12.getInstance() );
            service.setFaultHandler( new Soap12FaultHandler() );
        }
        else
        {
            final String msg = "Invalid soap version at " + configuration.getChild( "soapVersion" ).getLocation()
                + ". Must be 1.1 or 1.2.";
            throw new ConfigurationException( msg );
        }

        final Configuration[] properties = configuration.getChildren( "property" );

        for( int i = 0; i < properties.length; i++ )
        {
            service.setProperty( properties[i].getAttribute( "name" ), properties[i].getAttribute( "value" ) );
        }
    }
}
