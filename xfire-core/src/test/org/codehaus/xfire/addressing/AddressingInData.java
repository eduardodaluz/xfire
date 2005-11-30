package org.codehaus.xfire.addressing;

public class AddressingInData
{

    private AddressingHeaders  outHeaders;

    private AddressingHeaders  inHeaders;
    
    public AddressingHeaders getHeaders()
    {
        return outHeaders;
    }

    public void setOutHeaders(AddressingHeaders headers)
    {
        this.outHeaders = headers;
    }

    public AddressingHeaders getInHeaders()
    {
        return inHeaders;
    }

    public void setInHeaders(AddressingHeaders inHeaders)
    {
        this.inHeaders = inHeaders;
    }
    
}
