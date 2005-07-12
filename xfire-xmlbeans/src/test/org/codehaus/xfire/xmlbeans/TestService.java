package org.codehaus.xfire.xmlbeans;

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
}
