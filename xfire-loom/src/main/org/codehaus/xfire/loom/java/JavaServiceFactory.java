package org.codehaus.xfire.loom.java;

import javax.xml.namespace.QName;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

import org.codehaus.xfire.java.DefaultJavaService;
import org.codehaus.xfire.java.JavaService;
import org.codehaus.xfire.java.mapping.TypeMapping;
import org.codehaus.xfire.java.mapping.TypeMappingRegistry;
import org.codehaus.xfire.java.type.Type;
import org.codehaus.xfire.java.wsdl.JavaWSDLBuilder;
import org.codehaus.xfire.loom.simple.SimpleServiceFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.TransportManager;

/**
 * Creates and configures java-bound services for Loom.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class JavaServiceFactory extends SimpleServiceFactory implements Serviceable
{
    private TypeMappingRegistry m_typeMappingRegistry;
    private TransportManager m_transportManager;

    public void service( final ServiceManager manager ) throws ServiceException
    {
        m_typeMappingRegistry = (TypeMappingRegistry)manager.lookup( TypeMappingRegistry.ROLE );
        m_transportManager = (TransportManager)manager.lookup( TransportManager.ROLE );
    }

    public String getType()
    {
        return "java";
    }

    public Service createService( final Object target, final Configuration configuration )
        throws Exception
    {
        final DefaultJavaService s = new DefaultJavaService( getTypeMappingRegistry() );

        configureService( configuration, s, target );

        return s;
    }

    protected void configureService( final Configuration configuration,
                                     final DefaultJavaService service,
                                     final Object target )
        throws ConfigurationException
    {
        super.configureService( configuration, service, target );

        try
        {
            service.setServiceClass( configuration.getChild( JavaService.SERVICE_CLASS ).getValue() );
        }
        catch( ClassNotFoundException e )
        {
            final String msg = "Couldn't find service class at "
                + configuration.getChild( JavaService.SERVICE_CLASS ).getLocation();
            throw new ConfigurationException( msg, e );
        }

        // TODO use allowed methods attribute
        service.setProperty( JavaService.ALLOWED_METHODS,
                             configuration.getChild( JavaService.ALLOWED_METHODS ).getValue( "" ) );

        service.setAutoTyped( configuration.getChild( "autoTyped" ).getValueAsBoolean( false ) );
        service.initializeTypeMapping();

        final Configuration[] types = configuration.getChild( "types" ).getChildren( "type" );
        for( int i = 0; i < types.length; i++ )
        {
            initializeType( types[i], service.getTypeMapping() );
        }

        service.initializeOperations();

        service.setWSDLBuilder( new JavaWSDLBuilder( getTransportManager() ) );
    }

    private void initializeType( final Configuration configuration,
                                 final TypeMapping tm )
        throws ConfigurationException
    {
        try
        {
            final String ns = configuration.getAttribute( "namespace", tm.getEncodingStyleURI() );
            final String name = configuration.getAttribute( "name" );

            tm.register( loadClass( configuration.getAttribute( "class" ) ),
                         new QName( ns, name ),
                         (Type)loadClass( configuration.getAttribute( "type" ) ).newInstance() );
        }
        catch( ConfigurationException e )
        {
            throw e;
        }
        catch( Exception e )
        {
            throw new ConfigurationException( "Could not configure type at " + configuration.getLocation(), e );
        }
    }

    public TypeMappingRegistry getTypeMappingRegistry()
    {
        return m_typeMappingRegistry;
    }

    protected TransportManager getTransportManager()
    {
        return m_transportManager;
    }

    /**
     * Load a class from the class loader.
     *
     * @param className The name of the class.
     *
     * @return The class.
     */
    protected Class loadClass( String className )
        throws Exception
    {
        // Handle array'd types.
        if( className.endsWith( "[]" ) )
        {
            className = "[L" + className.substring( 0, className.length() - 2 ) + ";";
        }

        return getClass().getClassLoader().loadClass( className );
    }
}
