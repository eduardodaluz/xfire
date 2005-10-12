package org.codehaus.xfire.message.wrapped;

public class EchoFault
    extends Exception
{
    private String customMessage;

    public EchoFault()
    {
    }
    
    public EchoFault(String echo)
    {
        setCustomMessage(echo);
    }

    public String getCustomMessage()
    {
        return customMessage;
    }

    public void setCustomMessage(String customMessage)
    {
        this.customMessage = customMessage;
    }
}
