package org.codehaus.xfire.plexus.config;

import java.lang.reflect.Constructor;
import java.net.URL;

import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.handler.SoapHandler;
import org.codehaus.xfire.message.ObjectServiceHandler;
import org.codehaus.xfire.plexus.PlexusXFireComponent;
import org.codehaus.xfire.plexus.ServiceInvoker;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.object.DefaultObjectService;
import org.codehaus.xfire.service.object.ObjectService;
import org.codehaus.xfire.service.object.ObjectServiceBuilder;
import org.codehaus.xfire.service.object.ServiceBuilder;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.type.Type;
import org.codehaus.xfire.type.TypeMapping;
import org.codehaus.xfire.type.TypeMappingRegistry;

/**
 * Creates and configures services.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 20, 2004
 */
public class ObjectServiceConfigurator 
    extends PlexusXFireComponent
    implements Configurator
{
    public Service createService( PlexusConfiguration config ) 
        throws Exception
    {
        System.out.println("Creating service");
        String builderClass = config.getChild("serviceBuilder").getValue("");
        ServiceBuilder builder = getServiceBuilder(builderClass);

        String name = config.getChild("name").getValue();
        String namespace = config.getChild("namespace" ).getValue("");
        String use = config.getChild("use").getValue("literal");
        String style = config.getChild("style").getValue("wrapped");
        String serviceClass = config.getChild("serviceClass").getValue();
        String implClass = config.getChild("implementationClass").getValue("");
        String soapVersion = config.getChild("soapVersion").getValue("1.1");
        String wsdlUrl = config.getChild("wsdlURL").getValue("");

        DefaultObjectService service = null;
        if (wsdlUrl.length() > 0)
        {
            try
            {
                URL url = new URL(wsdlUrl);
                    
                builder.create(url);
            }
            catch (WSDLException e)
            {
                throw new PlexusConfigurationException("Could not load the WSDL file.", e);
            }
        }
        else
        {
            Class clazz = getClass().getClassLoader().loadClass(serviceClass);
            
            SoapVersion version = null;
            if (soapVersion.equals("1.1"))
            {
                version = Soap11.getInstance();
            }
            else if (soapVersion.equals("1.2"))
            {
                version = Soap12.getInstance();
            }
            
            service = (DefaultObjectService) builder.create(clazz, name, namespace, version, style, use);
        }
        
        if (implClass.length() > 0)
        {
            service.setProperty(ObjectService.SERVICE_IMPL_CLASS, loadClass(implClass));
        }

        final String scope = config.getChild( "scope" ).getValue( "application" );
        if( scope.equals( "application" ) )
            service.setScope( ObjectService.SCOPE_APPLICATION );
        else if( scope.equals( "session" ) )
            service.setScope( ObjectService.SCOPE_SESSION );
        else if( scope.equals( "request" ) )
            service.setScope( ObjectService.SCOPE_REQUEST );
        
        PlexusConfiguration[] types = config.getChild("types").getChildren("type");
        for ( int i = 0; i < types.length; i++ )
        {
            initializeType( types[i], service.getTypeMapping() );   
        }
        
        ServiceInvoker invoker = new ServiceInvoker(getServiceLocator());
        ObjectServiceHandler handler = new ObjectServiceHandler(invoker);
        SoapHandler sHandler = new SoapHandler(handler);
        service.setServiceHandler(sHandler);
        
        return service;
    }

    /**
     * @return
     * @throws PlexusConfigurationException 
     */
    protected ServiceBuilder getServiceBuilder(String builderClass) 
        throws Exception
    {
        if (builderClass.length() == 0)
        {
            return new ObjectServiceBuilder(getXFire(), getTypeMappingRegistry());
        }
        else
        {
            Class clz = getClass().getClassLoader().loadClass(builderClass);
            Constructor con = 
                clz.getConstructor( new Class[] {XFire.class, TypeMappingRegistry.class} );
            
            return (ServiceBuilder) 
                con.newInstance(new Object[] {getXFire(), getTypeMappingRegistry()});
        }
    }
    
    private void initializeType(PlexusConfiguration configuration, 
                                TypeMapping tm)
        throws PlexusConfigurationException
    {
        try
        {
            String ns = configuration.getAttribute("namespace", tm.getEncodingStyleURI());
            String name = configuration.getAttribute("name");
            
            Type type = (Type) loadClass( configuration.getAttribute("type") ).newInstance();

            tm.register( loadClass( configuration.getAttribute("class") ),
                         new QName( ns, name ),
                         type );
        }
        catch (Exception e)
        {
            if ( e instanceof PlexusConfigurationException )
                throw (PlexusConfigurationException) e;
            
            throw new PlexusConfigurationException( "Could not configure type.", e );
        }                     
    }
    
    public XFire getXFire()
    {
        XFire xfire = null;
        
        try
        {
            xfire = (XFire) getServiceLocator().lookup( XFire.ROLE );
        }
        catch (ComponentLookupException e)
        {
            throw new RuntimeException( "Couldn't find the XFire engine!", e );
        }

        return xfire;
    }
    
    public TypeMappingRegistry getTypeMappingRegistry()
    {
        TypeMappingRegistry registry = null;
        
        try
        {
            registry = (TypeMappingRegistry) getServiceLocator().lookup( TypeMappingRegistry.ROLE );
        }
        catch (ComponentLookupException e)
        {
            throw new RuntimeException( "Couldn't find the TypeMappingRegistry!", e );
        }

        return registry;
    }
    
    protected TransportManager getTransportManager()
    {
        TransportManager transMan = null;
        
        try
        {
            transMan = (TransportManager) getServiceLocator().lookup( TransportManager.ROLE );
        }
        catch (ComponentLookupException e)
        {
            throw new RuntimeException( "Couldn't find the TransportManager!", e );
        }
        
        return transMan;
    }
   
   /**
    * Load a class from the class loader.
    * 
    * @param className The name of the class.
    * @return The class.
    * @throws Exception
    */
   protected Class loadClass( String className )
       throws Exception
   {
       // Handle array'd types.
       if ( className.endsWith("[]") )
       {
           className = "[L" + className.substring(0, className.length() - 2 ) + ";";
       }
       
       return getClass().getClassLoader().loadClass( className );
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
