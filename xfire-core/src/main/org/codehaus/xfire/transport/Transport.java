package org.codehaus.xfire.transport;

import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.handler.HandlerPipeline;

/**
 * Transport
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface Transport
{
    String getName();

    HandlerPipeline getRequestPipeline();
    
    HandlerPipeline getResponsePipeline();
    
    FaultHandlerPipeline getFaultPipeline();
}
