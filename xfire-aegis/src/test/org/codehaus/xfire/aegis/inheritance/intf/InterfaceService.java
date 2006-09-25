package org.codehaus.xfire.aegis.inheritance.intf;

public class InterfaceService
    implements IInterfaceService
{
    public IChild getChild()
    {
        return new ChildImpl();
    }

    public IParent getChildViaParent()
    {
        return getChild();
    }

    public IGrandChild getGrandChild()
    {
        return new GrandChildImpl();
    }
}
