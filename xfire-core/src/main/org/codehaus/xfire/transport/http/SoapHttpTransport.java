package org.codehaus.xfire.transport.http;

import org.codehaus.xfire.soap.SoapTransport;
import org.codehaus.xfire.soap.SoapTransportHelper;
import org.codehaus.xfire.wsdl11.WSDL11Transport;

public class SoapHttpTransport
    extends HttpTransport
    implements WSDL11Transport, SoapTransport
{
    public static final String WSDL_SOAP_BINDING = "http://schemas.xmlsoap.org/wsdl/soap/http";

    public static final String SOAP11_HTTP_BINDING = "http://schemas.xmlsoap.org/soap/http";

    public static final String SOAP12_HTTP_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/";

    public SoapHttpTransport()
    {
        super();

        SoapTransportHelper.createSoapTransport(this);
    }

    public String[] getSupportedBindings()
    {
        return new String[] { SOAP11_HTTP_BINDING, SOAP12_HTTP_BINDING, WSDL_SOAP_BINDING };
    }

    public String getName()
    {
        return "Http";
    }
}
