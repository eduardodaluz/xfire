package org.codehaus.xfire.service.transport;

import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.handler.Handler;

/**
 * Mock implmentation of the <code>Transport</code> interface.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class MockTransport
        implements Transport
{
    private String address;

    public MockTransport(String address)
    {
        this.address = address;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public FaultHandlerPipeline getFaultPipeline()
    {
        return null;  //TODO: implement
    }

    public Handler getRequestHandler()
    {
        return null;  //TODO: implement
    }

    public Handler getResponseHandler()
    {
        return null;  //TODO: implement
    }
}

