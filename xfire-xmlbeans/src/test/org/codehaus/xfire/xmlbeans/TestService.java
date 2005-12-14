package org.codehaus.xfire.xmlbeans;

import junit.framework.Assert;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class TestService
{
    public ResponseDocument GetWeatherByZipCode( RequestDocument body )
    {
        ResponseDocument response = ResponseDocument.Factory.newInstance();
        return response;
    }

    public TroubleDocument GetTrouble(TroubleDocument trouble)
    {
        return trouble;
    }
    
    public TroubleDocument ThrowFault() throws CustomFault
    {
        throw new CustomFault();
    }
    
    public ResponseDocument mixedRequest(String string, RequestDocument req)
    {
        Assert.assertEquals("foo", string);
        Assert.assertEquals("foo", req.getRequest().getSessionId());
        
        ResponseDocument response = ResponseDocument.Factory.newInstance();
        response.addNewResponse().addNewForm();
        return response;
    }
}
