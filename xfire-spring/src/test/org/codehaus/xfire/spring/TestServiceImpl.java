package org.codehaus.xfire.spring;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class TestServiceImpl
    implements TestService
{

    
    public String returnEcho(String value)
    {
        
        return value;
    }

}
