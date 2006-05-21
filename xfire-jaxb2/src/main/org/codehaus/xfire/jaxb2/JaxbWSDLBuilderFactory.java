package org.codehaus.xfire.jaxb2;

import javax.wsdl.WSDLException;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilderFactory;

/**
 */
public class JaxbWSDLBuilderFactory
    implements WSDLBuilderFactory
{
    public WSDLBuilder createWSDLBuilder(Service service, TransportManager transportManager)
    {
        try
        {
            return new JaxbWSDLBuilder(service, transportManager);
        }
        catch (WSDLException e)
        {
            throw new XFireRuntimeException("Error creating the WSDLBuilder", e);
        }
    }
}
