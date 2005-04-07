package org.codehaus.xfire.fault;

import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.codehaus.xfire.XFireRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <p/>
 * In XFire, applications throw their own declared exceptions which are then turned into faults.  The XFireFault class
 * wraps these exceptions extracting out the details for the fault message. </p>
 * <p/>
 * If the developer wishes to generate their own custom fault messages, they can either override XFireFault to provide
 * the FaultHandlers with the necessary information or write a new FaultHandler. </p>
 * <p/>
 * TODO Add i18n support </p>
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 14, 2004
 */
public class XFireFault
        extends Exception
{
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
     * <p/>
     * A SOAP 1.2 only fault code. </p>
     * <p/>
     * "The message could not be processed for reasons attributable to the processing of the message rather than to the
     * contents of the message itself." -- SOAP 1.2 Spec </p>
     * <p/>
     * If this message is used in a SOAP 1.1 Fault it will most likely (depending on the FaultHandler) be mapped to
     * "Sender" instead. </p>
     */
    public final static String RECEIVER = "Receiver";

    private String faultCode;

    private String subCode;

    private String message;

    private String role;

    private Node detail;

    private DocumentBuilder docBuilder;

    private Map namespaces;

    /**
     * Create a fault.
     *
     * @param throwable The exception which caused this fault.
     * @param code      The fault code. See XFireFault's static fields.
     */
    public XFireFault(String message,
                      Throwable throwable,
                      String code)
    {
        super(throwable);

        if (message != null)
            this.message = message;
        else
            this.message = "Fault";

        this.faultCode = code;
        this.namespaces = new HashMap();
    }

    /**
     * Create a fault for the specified exception. The faultCode is set to RECEIVER.
     *
     * @param throwable
     */
    public XFireFault(Throwable throwable)
    {
        this(throwable, RECEIVER);
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
        super();
        this.message = message;
        this.faultCode = code;
        this.namespaces = new HashMap();
    }

    protected XFireFault()
    {
    }

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
     * @return The SubCode element as detailed by the SOAP 1.2 spec.
     */
    public String getSubCode()
    {
        return subCode;
    }

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
                throw new XFireRuntimeException("Couldn't find a DOM parser.", e);
            }
        }
        return detail;
    }

    public void setDetail(Node details)
    {
        detail = details;
    }

    public void setSubCode(String subCode)
    {
        this.subCode = subCode;
    }

    public String getFaultCode()
    {
        return faultCode;
    }

    public void setFaultCode(String faultCode)
    {
        this.faultCode = faultCode;
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

    public void addNamespace(String prefix, String ns)
    {
        namespaces.put(prefix, ns);
    }

    /**
     * @return
     */
    public boolean hasDetails()
    {
        if (detail == null)
            return false;

        return true;
    }

    /**
     * @return Returns the message.
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
     * @return Returns the fault actor.
     */
    public String getRole()
    {
        return role;
    }

    /**
     * Sets the fault actor.
     *
     * @param actor
     */
    public void setRole(String actor)
    {
        this.role = actor;
    }
}
