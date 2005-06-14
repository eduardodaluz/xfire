package org.codehaus.xfire.loom.type;

import javax.xml.namespace.QName;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.basic.BooleanType;
import org.codehaus.xfire.aegis.type.basic.DoubleType;
import org.codehaus.xfire.aegis.type.basic.FloatType;
import org.codehaus.xfire.aegis.type.basic.IntType;
import org.codehaus.xfire.aegis.type.basic.LongType;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.util.ClassLoaderUtils;

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

            final Class clazz = ClassLoaderUtils.loadClass( configuration.getAttribute( "class" ), getClass() );
            final Class typeClass = ClassLoaderUtils.loadClass( configuration.getAttribute( "type" ), getClass() );

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
}
