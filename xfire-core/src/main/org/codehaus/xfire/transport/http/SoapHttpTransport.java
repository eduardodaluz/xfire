package org.codehaus.xfire.transport.http;

import org.codehaus.xfire.soap.SoapTransport;

public class SoapHttpTransport
    extends HttpTransport
{
    public static final String SOAP11_HTTP_BINDING = "http://schemas.xmlsoap.org/soap/http";

    public static final String SOAP12_HTTP_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/";

    public SoapHttpTransport()
    {
        super();

        SoapTransport.createSoapTransport(this);
    }

    public String[] getSupportedBindings()
    {
        return new String[] { SOAP11_HTTP_BINDING, SOAP12_HTTP_BINDING };
    }
}
