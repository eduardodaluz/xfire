package org.codehaus.xfire.loom.type;

import javax.xml.namespace.QName;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.type.Type;
import org.codehaus.xfire.type.TypeMapping;
import org.codehaus.xfire.type.basic.BooleanType;
import org.codehaus.xfire.type.basic.DoubleType;
import org.codehaus.xfire.type.basic.FloatType;
import org.codehaus.xfire.type.basic.IntType;
import org.codehaus.xfire.type.basic.LongType;

/**
 * Extends and configures the TypeMappingRegistry.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 31, 2004
 */
public class TypeMappingRegistry extends DefaultTypeMappingRegistry implements LogEnabled, Configurable
{
    private Logger logger;

    public TypeMappingRegistry()
    {
        super( true );
    }

    public void configure( final Configuration config )
        throws ConfigurationException
    {
        final Configuration[] tmConfig = config.getChildren( "typeMapping" );

        for( int i = 0; i < tmConfig.length; i++ )
        {
            configureTypeMapping( tmConfig[i] );
        }
    }

    private void configureTypeMapping( final Configuration configuration )
        throws ConfigurationException
    {
        final String namespace = configuration.getAttribute( "namespace" );
        TypeMapping tm = getTypeMapping( namespace );

        if( null == tm )
        {
            final String parentNamespace = configuration.getAttribute( "parentNamespace",
                                                                       getDefaultTypeMapping().getEncodingStyleURI() );

            tm = createTypeMapping( parentNamespace, false );

            register( namespace, tm );

            if( configuration.getAttributeAsBoolean( "default", false ) )
            {
                registerDefault( tm );
            }

            // register primitive types manually since there is no way
            // to do Class.forName("boolean") et al.
            tm.register( boolean.class, new QName( SoapConstants.XSD, "boolean" ), new BooleanType() );
            tm.register( int.class, new QName( SoapConstants.XSD, "int" ), new IntType() );
            tm.register( double.class, new QName( SoapConstants.XSD, "double" ), new DoubleType() );
            tm.register( float.class, new QName( SoapConstants.XSD, "float" ), new FloatType() );
            tm.register( long.class, new QName( SoapConstants.XSD, "long" ), new LongType() );
        }

        final Configuration[] types = configuration.getChildren( "type" );

        for( int i = 0; i < types.length; i++ )
        {
            configureType( types[i], tm );
        }
    }

    private void configureType( final Configuration configuration, final TypeMapping tm )
        throws ConfigurationException
    {
        try
        {
            final String ns = configuration.getAttribute( "namespace" );
            final String name = configuration.getAttribute( "name" );
            final QName qname = new QName( ns, name );

            final Class clazz = loadClass( configuration.getAttribute( "class" ) );
            final Class typeClass = loadClass( configuration.getAttribute( "type" ) );

            tm.register( clazz,
                         qname,
                         (Type)typeClass.newInstance() );

            logger.debug( "Registered " + typeClass.getName() + " for " + qname + " with class " + clazz.getName() );
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

    public void enableLogging( final Logger logger )
    {
        this.logger = logger;
    }

    protected Class loadClass( final String className )
        throws ClassNotFoundException
    {
        try
        {
            return getClass().getClassLoader().loadClass( className );
        }
        catch( ClassNotFoundException cnfe )
        {
            try
            {
                return Class.forName( className );
            }
            catch( ClassNotFoundException cnf2 )
            {
                return Thread.currentThread().getContextClassLoader().loadClass( className );
            }
        }
    }
}
