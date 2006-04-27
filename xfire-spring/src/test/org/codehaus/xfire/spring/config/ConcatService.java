package org.codehaus.xfire.spring.config;

public class ConcatService
{
    public String concat(String s1, String s2)
    {
        return s1 + s2;
    }
    
    public String concat(String s1, String s2, String s3)
    {
        return s1 + s2 + s3;
    }
    
    public String exluded(String s)
    {
        return s;
    }
}
