package org.codehaus.xfire.wsdl11.builder;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.TransportManager;

/**
 * Creates a WSDLBuilder for a service.
 */
public class DefaultWSDLBuilderFactory
    implements WSDLBuilderFactory
{
    public DefaultWSDLBuilderFactory()
    {
    }

    public WSDLBuilder createWSDLBuilder(Service service, TransportManager transportManager)
    {
        try
        {
            return new WSDLBuilder(service, transportManager);
        }
        catch (XFireRuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException("Could not create wsdl builder", e);
        }
    }
}
