package org.codehaus.xfire;


/**
 * SOAP constants from the specs.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public class SOAPConstants
{
    /** SOAP 1.1 Declarations. */
    
    public final static String SOAP11_ENVELOPE_NS = "http://schemas.xmlsoap.org/soap/envelope/";
    
    public final static String SOAP11_ENCODED = "http://schemas.xmlsoap.org/soap/encoding/";

   
    /** SOAP 1.2 Declarations. */
    public final static String SOAP12_ENVELOPE_NS = "http://www.w3.org/2003/05/soap-envelope";
    
    public final static String SOAP12_ENCODED = "http://www.w3.org/2003/05/soap-encoding";

    
    /** Document styles. */
    
    public static final String STYLE_RPC = "rpc";

    public static final String STYLE_DOCUMENT = "document";

    public static final String STYLE_WRAPPED = "wrapped";

    public static final String STYLE_MESSAGE = "message";

    
    public static final String USE_LITERAL = "literal";

    public static final String USE_ECNODED = "encoded";

    /** XML Schema Namespace. */
    public static final String XSD = "http://www.w3.org/2001/XMLSchema";

}
