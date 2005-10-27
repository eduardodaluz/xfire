package org.codehaus.xfire.util;

import java.util.ArrayList;

public class FastStack
    extends ArrayList
{
    public void push(Object o)
    {
        add(o);
    }
    
    public Object pop()
    {
        return remove(size()-1);
    }
    
    public boolean empty()
    {
        return size() == 0;
    }

    public Object peek()
    {
        return get(size()-1);
    }
}
