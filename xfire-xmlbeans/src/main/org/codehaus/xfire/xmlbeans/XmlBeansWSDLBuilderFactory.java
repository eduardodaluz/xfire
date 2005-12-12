package org.codehaus.xfire.xmlbeans;

import javax.wsdl.WSDLException;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilderFactory;

/**
 */
public class XmlBeansWSDLBuilderFactory
    implements WSDLBuilderFactory
{
    public WSDLBuilder createWSDLBuilder(Service service, TransportManager transportManager)
    {
        try
        {
            return new XmlBeansWSDLBuilder(service, transportManager);
        }
        catch (WSDLException e)
        {
            throw new XFireRuntimeException("error creating wsdlbuilder", e);
        }
    }
}
