package org.codehaus.xfire.wsdl;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A WSDL Document for a service.  This is supposed to be version
 * agnostic as well as let you generate/retrieve WSDL in whatever
 * way you want.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface WSDL
{
    public static final String WSDL11_NS = "http://schemas.xmlsoap.org/wsdl/";
    
    public static final String WSDL11_SOAP_NS = "http://schemas.xmlsoap.org/wsdl/soap/";

    /**
     * Write the WSDL to an OutputStream.
     * 
     * @param out The OutputStream.
     * @throws IOException
     */
    void write(OutputStream out) throws IOException;
}