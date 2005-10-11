package org.codehaus.xfire.wsdl11.builder;

import java.util.List;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.wsdl11.WSDL11ParameterBinding;

/**
 * Creates a WSDLBuilder for a service.
 */
public class DefaultWSDLBuilderFactory
    implements WSDLBuilderFactory
{
    private List schemaLocations;
    
    public DefaultWSDLBuilderFactory()
    {
    }

    public DefaultWSDLBuilderFactory(List schemas)
    {
        setSchemaLocations(schemas);
    }

    /**
     * A List of Strings which designate schema locations on the filesystem or classpath.
     * @return
     */
    public List getSchemaLocations()
    {
        return schemaLocations;
    }

    public void setSchemaLocations(List schemaLocations)
    {
        this.schemaLocations = schemaLocations;
    }

    public WSDLBuilder createWSDLBuilder(Service service,
                                         WSDL11ParameterBinding paramBinding,
                                         TransportManager transportManager)
    {
        try
        {
            WSDLBuilder builder = new WSDLBuilder(service, transportManager, paramBinding);
            
            if (getSchemaLocations() != null)
                builder.addSchemas(getSchemaLocations());
            
            return builder;
        }
        catch (XFireRuntimeException e)
        {
            throw (XFireRuntimeException) e;
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException("Could not create wsdl builder", e);
        }
    }
}
