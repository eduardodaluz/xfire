package org.codehaus.xfire.generator.oneway;

import javax.jws.WebService;

import jsr181.jaxb.oneway.SendMessagePortType;

@WebService(serviceName = "SendMessage", targetNamespace = "http://xfire.codehaus.org/test/echo", endpointInterface = "jsr181.jaxb.oneway.SendMessagePortType")
public class SendMessageImpl
    implements SendMessagePortType
{

    private String message;

    public void sendMessage(String sendMessage)
    {
        this.message = sendMessage;
    }

    public String getMessage()
    {
        return message;
    }

}
