package org.codehaus.xfire;

import java.io.OutputStream;

public class MessageDestination
{
    private String uri;
    private OutputStream stream;
    
    public MessageDestination(OutputStream stream, String uri)
    {
        this.stream = stream;
        this.uri = uri;
    }
    
    public OutputStream getOutputStream()
    {
        return stream;
    }
    
    public String getUri()
    {
        return uri;
    }
}
