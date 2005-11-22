package org.codehaus.xfire.wsdl11.builder;

import org.codehaus.xfire.service.Service;

/**
 * Creates a WSDLBuilder for a service.
 */
public interface WSDLBuilderFactory
{
    WSDLBuilder createWSDLBuilder(Service service);
}
