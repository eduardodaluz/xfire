package org.codehaus.xfire.security;

public class CallbackInfo
{

    private String id;

    private boolean hashed;

    private int usage;

    public boolean isHashed()
    {
        return hashed;
    }
 
    public void setHashed(boolean hashed)
    {
        this.hashed = hashed;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public int getUsage()
    {
        return usage;
    }

    public void setUsage(int usage)
    {
        this.usage = usage;
    }
    
    
}
