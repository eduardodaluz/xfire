package org.codehaus.xfire.loom;

import java.net.URL;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.object.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.type.TypeMapping;
import org.codehaus.xfire.type.TypeMappingRegistry;

/**
 * Default implementation of a ServiceFactory
 *
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class DefaultServiceFactory extends AbstractLogEnabled implements ServiceFactory, Serviceable, Initializable
{
    private TypeMappingRegistry m_typeMappingRegistry;
    private TransportManager m_transportManager;

    private ObjectServiceFactory m_factory;

    public void service( final ServiceManager manager ) throws ServiceException
    {
        m_typeMappingRegistry = (TypeMappingRegistry)manager.lookup( TypeMappingRegistry.ROLE );
        m_transportManager = (TransportManager)manager.lookup( TransportManager.ROLE );
    }

    protected final TransportManager getTransportManager()
    {
        return m_transportManager;
    }

    protected final TypeMappingRegistry getTypeMappingRegistry()
    {
        return m_typeMappingRegistry;
    }

    protected final void setFactory( final ObjectServiceFactory factory )
    {
        m_factory = factory;
    }

    public void initialize() throws Exception
    {
        setFactory( new ObjectServiceFactory( m_transportManager, m_typeMappingRegistry ) );
    }

    public Service create( final Class clazz )
    {
        return m_factory.create( clazz );
    }

    public Service create( final Class clazz,
                           final String name,
                           final String namespace,
                           final SoapVersion version,
                           final String style,
                           final String use,
                           final String encodingStyleURI )
    {
        return m_factory.create( clazz, name, namespace, version, style, use, encodingStyleURI );
    }

    public Service create( final Class clazz, final TypeMapping tm, final URL wsdlUrl ) throws Exception
    {
        return m_factory.create( clazz, tm, wsdlUrl );
    }

    public Service create( final Class clazz, final SoapVersion version, final String style, final String use )
    {
        return m_factory.create( clazz, version, style, use );
    }
}