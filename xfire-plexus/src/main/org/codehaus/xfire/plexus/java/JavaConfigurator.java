package org.codehaus.xfire.plexus.java;

import javax.xml.namespace.QName;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.xfire.java.DefaultJavaService;
import org.codehaus.xfire.java.mapping.TypeMapping;
import org.codehaus.xfire.java.mapping.TypeMappingRegistry;
import org.codehaus.xfire.java.type.Type;
import org.codehaus.xfire.java.wsdl.JavaWSDLBuilder;
import org.codehaus.xfire.plexus.config.Configurator;
import org.codehaus.xfire.plexus.config.PlexusConfigurationAdapter;
import org.codehaus.xfire.plexus.simple.SimpleConfigurator;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.TransportManager;

/**
 * Creates and configures java-bound services for plexus.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 20, 2004
 */
public class JavaConfigurator
    extends SimpleConfigurator
    implements Configurator
{
    final public static String SERVICE_TYPE = "java";
    
    /**
     * @see org.codehaus.xfire.plexus.config.Configurator#getServiceType()
     */
    public String getServiceType()
    {
        return SERVICE_TYPE;
    }

    /**
     * @see org.codehaus.xfire.plexus.config.Configurator#createService(org.codehaus.plexus.configuration.PlexusConfiguration)
     */
    public Service createService( PlexusConfiguration config ) 
	    throws Exception
	{
	    DefaultJavaService s = new DefaultJavaService(getTypeMappingRegistry());
        
	    configureService(config, s);
	
	    getServiceRegistry().register(s);
		
	    return s;
	}

    protected void configureService(PlexusConfiguration config, DefaultJavaService s)
        throws PlexusConfigurationException
    {
        super.configureService(config, s);
        
        s.initializeTypeMapping();
	    
	    PlexusConfiguration[] types = config.getChild("types").getChildren("type");
	    for ( int i = 0; i < types.length; i++ )
        {
            initializeType( types[i], s.getTypeMapping() );   
        }
	    
	    s.initializeOperations();

	    s.setWSDLBuilder( new JavaWSDLBuilder( getTransportManager() ) );
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
            type.configure(new PlexusConfigurationAdapter(configuration));
            
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
}
