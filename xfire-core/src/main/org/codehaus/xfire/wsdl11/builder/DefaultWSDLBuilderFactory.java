package org.codehaus.xfire.wsdl11.builder;

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
    public WSDLBuilder createWSDLBuilder(Service service,
                                         WSDL11ParameterBinding paramBinding,
                                         TransportManager transportManager)
    {
        try
        {
            return new WSDLBuilder(service, transportManager, paramBinding);
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
