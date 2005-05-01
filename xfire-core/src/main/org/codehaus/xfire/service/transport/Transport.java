package org.codehaus.xfire.service.transport;

import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.handler.Handler;

/**
 * Transport
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public interface Transport
{
    String getAddress();

    Handler getRequestHandler();

    Handler getResponseHandler();

    FaultHandlerPipeline getFaultPipeline();
}
