package org.codehaus.xfire.fault;

import java.util.List;
import org.w3c.dom.Element;

/**
 * <p>
 * In XFire, applications throw their own declared exceptions which are
 * then turned into faults.  The XFireFault class wraps these exceptions
 * extracting out the details for the fault message.
 * </p>
 * <p>
 * If the developer wishes to generate their own custom fault messages,
 * they can either override XFireFault to provide the FaultHandlers with
 * the necessary information or write a new FaultHandler.
 * </p>
 * <p>
 * TODO Add i18n support
 * </p>
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 14, 2004
 */
public class XFireFault
    extends Exception
{
    /** Fault codes. */
    public final static String VERSION_MISMATCH = "VersionMismatch";
    public final static String MUST_UNDERSTAND = "MustUnderstand";
    public final static String DATA_ENCODING_UNKNOWN = "DataEncodingUnknown";
    
    /**
     * "The message was incorrectly formed or did not contain the appropriate 
     * information in order to succeed." -- SOAP 1.2 Spec
     */
    public final static String SENDER = "Sender";
    
    /** 
     * "The message could not be processed for reasons attributable to the
     * processing of the message rather than to the contents of the message itself."
     *  -- SOAP 1.2 Spec
     */
    public final static String RECEIVER = "Receiver";

	private String faultCode;
    
	private Element subCode;
	
	private List detail;
	
	private Exception exception;
	
    /**
     * Create a fault.
     * 
	 * @param string The fault message.
	 * @param exception The exception which caused this fault.
     * @param code The fault code. See XFireFault's static fields.
	 */
	public XFireFault( String message, 
	                   Exception exception,
	                   String code )
	{
		super( message, exception );
		this.faultCode = code;
	}

    /**
     * Create a fault for the specified exception. The faultCode is
     * set to RECEIVER.
     * @param exception
     */
	public XFireFault( Exception exception )
	{
	    super( exception );
	    this.faultCode = RECEIVER;
	}
	
	/**
     * Create a fault with the specified faultCode. The exception
     * message is used for the fault message.
     * 
     * @param exception The exception that caused this fault.
     * @param code The fault code. See XFireFault's static fields.
     */
    public XFireFault(Exception exception, String code)
    {
        super( exception.getMessage(), exception );
        this.faultCode = code;
    }

    /**
     * Create an exception wih the specified fault message
     * and faultCode.
     * 
     * @param message The fault message.
	 * @param code The fault code. See XFireFault's static fields.
     */
	public XFireFault(String message, String code)
	{
		super( message );
        this.faultCode = code;
	}

	public static XFireFault createFault( Exception e )
	{
	    XFireFault fault = null;
        
        if ( e instanceof XFireFault )
        {
            fault = (XFireFault) e;
        }
        else
        {
            fault = new XFireFault( e );
        }
        
        return fault;
	}
	
    /**
     * @return
     */
    public String getCode()
    {
        return faultCode;
    }
    
    public String getReason()
    {
        return getMessage();
    }
    
    /**
     * Returns the SubCode for the Fault Code.
     * 
     * TODO: The spec seems unclear whether or not there can be multiple
     * subcodes.  Its an easy enough change to support multiple subcodes.
     * 
     * @return The SubCode element as detailed by the SOAP 1.2 spec.
     */
    public Element getSubCode()
    {
        return subCode;
    }
	
    public void setSubCode(Element subCode)
    {
        this.subCode = subCode;
    }
    
    /**
     * A list of Element's that provide the message detail.
     * @return
     */
    public List getDetail()
    {
        return detail;
    }
    
    public void setDetail( List detail )
    {
        this.detail = detail;
    }
}
