package org.codehaus.xfire.fault;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.codehaus.xfire.XFireRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * In XFire, applications throw their own declared exceptions which are then turned into faults.  The
 * <code>XFireFault</code> class wraps these exceptions extracting out the details for the fault message.
 * <p/>
 * If the developer wishes to generate their own custom fault messages, they can either override XFireFault to provide
 * the FaultHandlers with the necessary information or write a new FaultHandler. </p>
 * <p/>
 * TODO Add i18n support
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 14, 2004
 */
public class XFireFault
        extends Exception
{
    /**
	 * Serialization ID. 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
     * Fault codes.
     */
    public final static String VERSION_MISMATCH = "VersionMismatch";
    public final static String MUST_UNDERSTAND = "MustUnderstand";
    public final static String DATA_ENCODING_UNKNOWN = "DataEncodingUnknown";

    /**
     * "The message was incorrectly formed or did not contain the appropriate information in order to succeed." -- SOAP
     * 1.2 Spec
     */
    public final static String SENDER = "Sender";

    /**
     * A SOAP 1.2 only fault code.
     * <p/>
     * "The message could not be processed for reasons attributable to the processing of the message rather than to the
     * contents of the message itself." -- SOAP 1.2 Spec
     * <p/>
     * If this message is used in a SOAP 1.1 Fault it will most likely (depending on the FaultHandler) be mapped to
     * "Sender" instead.
     */
    public final static String RECEIVER = "Receiver";

    private String faultCode;
    private String subCode;
    private String message;
    private String role;
    private Node detail;
    private Map namespaces;
    private Throwable cause;

    /**
     * Creates a <code>XFireFault</code> from the given throwable. If the throwable is a <code>XFireFault</code>, it is
     * not wrapped.
     *
     * @param throwable the throwable
     * @return the fault
     */
    public static XFireFault createFault(Throwable throwable)
    {
        XFireFault fault = null;

        if (throwable instanceof XFireFault)
        {
            fault = (XFireFault) throwable;
        }
        else
        {
            fault = new XFireFault(throwable);
        }

        return fault;
    }

    protected XFireFault()
    {
    }

    /**
     * Create a fault for the specified exception. The faultCode is set to RECEIVER.
     *
     * @param throwable
     */
    public XFireFault(Throwable throwable)
    {
        this(throwable.getMessage(), throwable, RECEIVER);
    }

    /**
     * Create a fault with the specified faultCode. The exception message is used for the fault message.
     *
     * @param throwable The exception that caused this fault.
     * @param code      The fault code. See XFireFault's static fields.
     */
    public XFireFault(Throwable throwable, String code)
    {
        this(throwable.getMessage(), throwable, code);
    }

    /**
     * Create an exception wih the specified fault message and faultCode.
     *
     * @param message The fault message.
     * @param code    The fault code. See XFireFault's static fields.
     */
    public XFireFault(String message, String code)
    {
        this(message, null, code);
    }

    /**
     * Create a fault.
     *
     * @param cause The exception which caused this fault.
     * @param code  The fault code. See XFireFault's static fields.
     */
    public XFireFault(String message,
                      Throwable cause,
                      String code)
    {
        this.message = message != null ? message : "Fault";
        this.faultCode = code;
        this.cause = cause;
        this.namespaces = new HashMap();
    }

    /**
     * Adds a namespace with prefix to this fault.
     *
     * @param prefix the prefix
     * @param ns     the namespace.
     */
    public void addNamespace(String prefix, String ns)
    {
        namespaces.put(prefix, ns);
    }

    /**
     * Prints this throwable and its backtrace to the specified print stream.
     *
     * @param s <code>PrintStream</code> to use for output
     */
    public void printStackTrace(PrintStream s)
    {
        if (this.cause == null || this.cause == this)
        {
            super.printStackTrace(s);
        }
        else
        {
            s.println(this);
            this.cause.printStackTrace(s);
        }
    }

    /**
     * Prints this throwable and its backtrace to the specified print writer.
     *
     * @param w <code>PrintWriter</code> to use for output
     */
    public void printStackTrace(PrintWriter w)
    {
        if (this.cause == null || this.cause == this)
        {
            super.printStackTrace(w);
        }
        else
        {
            w.println(this);
            this.cause.printStackTrace(w);
        }
    }

    /**
     * Returns the cause of this throwable or <code>null</code> if the  cause is nonexistent or unknown.
     *
     * @return the nested cause.
     */
    public Throwable getCause()
    {
        return (this.cause == this ? null : this.cause);
    }

    /**
     * Returns the detail node. If no detail node has been set, an empty <code>&lt;detail&gt;</code> is created.
     *
     * @return the detail node.
     */
    public Node getDetail()
    {
        if (detail == null)
        {
            try
            {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder b = factory.newDocumentBuilder();

                Document doc = b.newDocument();
                detail = doc.createElement("detail");
            }
            catch (ParserConfigurationException e)
            {
                throw new XFireRuntimeException("Couldn't find a DOM parser", e);
            }
        }
        return detail;
    }

    /**
     * Sets a details <code>Node</code> on this fault.
     *
     * @param details the detail node.
     */
    public void setDetail(Node details)
    {
        detail = details;
    }

    /**
     * Returns the fault code of this fault.
     *
     * @return the fault code.
     */
    public String getFaultCode()
    {
        return faultCode;
    }

    /**
     * Sets the fault code of this fault.
     *
     * @param faultCode the fault code.
     */
    public void setFaultCode(String faultCode)
    {
        this.faultCode = faultCode;
    }

    /**
     * Returns the detail message string of this fault.
     *
     * @return the detail message string of this <code>XfireFault</code> (which may be <code>null</code>)
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param message The message to set.
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * User defined namespaces which will be written out on the resultant SOAP Fault (for use easy with SubCodes and
     * Detail) elements.
     *
     * @return
     */
    public Map getNamespaces()
    {
        return namespaces;
    }

    public String getReason()
    {
        return getMessage();
    }

    /**
     * Returns the fault actor.
     *
     * @return the fault actor.
     */
    public String getRole()
    {
        return role;
    }

    /**
     * Sets the fault actor.
     *
     * @param actor the actor.
     */
    public void setRole(String actor)
    {
        this.role = actor;
    }

    /**
     * Returns the SubCode for the Fault Code.
     *
     * @return The SubCode element as detailed by the SOAP 1.2 spec.
     */
    public String getSubCode()
    {
        return subCode;
    }

    /**
     * Sets the SubCode for the Fault Code.
     *
     * @param subCode The SubCode element as detailed by the SOAP 1.2 spec.
     */
    public void setSubCode(String subCode)
    {
        this.subCode = subCode;
    }

    /**
     * Indicates whether this fault has a detail message.
     *
     * @return <code>true</code> if this fault has a detail message; <code>false</code> otherwise.
     */
    public boolean hasDetails()
    {
        return detail == null ? false : true;
    }
}

