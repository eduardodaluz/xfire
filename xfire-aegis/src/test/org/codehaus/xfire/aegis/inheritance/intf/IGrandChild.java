package org.codehaus.xfire.aegis.inheritance.intf;

public interface IGrandChild extends IChild {
    String getGrandChildName();
    
    // make sure we don't get two getChildName props
    String getChildName();
}
