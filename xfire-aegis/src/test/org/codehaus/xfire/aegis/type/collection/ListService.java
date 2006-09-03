package org.codehaus.xfire.aegis.type.collection;

import java.util.ArrayList;
import java.util.List;

public class ListService
{
    public List getStrings()
    {
        List strings = new ArrayList();
        
        strings.add("bleh");
        
        return strings;
    }
    
    public List getDoubles()
    {
        List doubles = new ArrayList();
        
        doubles.add(new Double(1.0));
        
        return doubles;
    }
    
    public List getListofListofDoubles()
    {
        List l = new ArrayList();
        List doubles = new ArrayList();
        
        doubles.add(new Double(1.0));
        
        l.add(doubles);
        return l;
    }
    
    public void receiveStrings(List strings)
    {
    }
    
    public void receiveDoubles(List doubles)
    {
    }
}