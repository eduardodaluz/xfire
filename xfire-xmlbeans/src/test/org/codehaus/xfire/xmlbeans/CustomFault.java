package org.codehaus.xfire.xmlbeans;

public class CustomFault extends Exception
{
    private String extraInfo;
    public CustomFault()
    {
        super("custom fault");
    }
    public String getExtraInfo()
    {
        return extraInfo;
    }
    public void setExtraInfo(String extraInfo)
    {
        this.extraInfo = extraInfo;
    }
}
