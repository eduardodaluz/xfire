package org.codehaus.xfire.aegis.inheritance.intf;

public class GrandChildImpl
    implements IGrandChild
{

    public String getChildName()
    {
        return "child";
    }

    public String getParentName()
    {
        return "parent";
    }

    public String getGrandChildName()
    {
        return "grandchild";
    }

}
