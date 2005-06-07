package org.codehaus.xfire.type.basic.intf;

public class BeanServiceImpl
    implements BeanServiceIntf
{
    public BeanIntf getBeanIntf()
    {
        BeanImpl b = new BeanImpl();
        b.setName("name");
        return b;
    }

    public String getStuff()
    {
        return "stuff";
    }
}
