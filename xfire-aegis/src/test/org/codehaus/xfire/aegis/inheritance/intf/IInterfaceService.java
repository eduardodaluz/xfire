package org.codehaus.xfire.aegis.inheritance.intf;

public interface IInterfaceService {
    IChild getChild();
    
    IParent getChildViaParent();
    
    IGrandChild getGrandChild();
}
