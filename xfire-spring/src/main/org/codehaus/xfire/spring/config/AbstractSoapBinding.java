package org.codehaus.xfire.spring.config;

public abstract class AbstractSoapBinding
{
    private String transport;
    
    public String getTransport()
    {
        return transport;
    }

    public void setTransport(String transport)
    {
        this.transport = transport;
    }
}
