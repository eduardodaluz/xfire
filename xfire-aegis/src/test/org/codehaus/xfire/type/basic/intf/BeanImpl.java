package org.codehaus.xfire.type.basic.intf;

public class BeanImpl
    implements BeanIntf
{
    private String name;
    
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

}
