package org.codehaus.xfire.type.collection;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

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
    
    public void receiveStrings(List strings)
    {
        Assert.assertNotNull(strings);
        Assert.assertEquals(1, strings.size());
    }
    
    public void receiveDoubles(List doubles)
    {
        Assert.assertNotNull(doubles);
        Assert.assertEquals(1, doubles.size());
    }
}