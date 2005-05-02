package org.codehaus.xfire.service.transport;

import org.codehaus.xfire.MessageContext;

/**
 * Mock implmentation of the <code>Transport</code> interface.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class MockTransport
        implements Transport
{
    private String address;
    private String locationURI;

    public MockTransport(String address, String locationURI)
    {
        this.address = address;
        this.locationURI = locationURI;
    }

    public MessageContext createMessageContext()
    {
        return null;  //TODO: implement
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getTransportURI()
    {
        return locationURI;
    }
}

