package org.codehaus.xfire.spring;

public class TestServiceImpl
    implements TestService
{

    
    public String returnEcho(String value)
    {
        
        return value;
    }

}
