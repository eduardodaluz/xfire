package org.codehaus.xfire.message.document;

/**
 * A service with methods which are not easily distinguished
 * in a document style service.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DocumentService
{
    public String getString1()
    {
        return "string";
    }
    
    public String getString2( String bleh )
    {
        return bleh;
    }
    
    public String getString3( String bleh, String bleh2 )
    {
        return bleh+bleh2;
    }
}
