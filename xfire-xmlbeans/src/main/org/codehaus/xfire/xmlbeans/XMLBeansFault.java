package org.codehaus.xfire.xmlbeans;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.SOAPConstants;
import org.codehaus.xfire.fault.XFireFault;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 17, 2004
 */
public class XMLBeansFault
    extends XFireFault
{
    private static final long serialVersionUID = 1L;
    
    private XmlObject fault;
    
    /**
     * @param fault The XmlObject representing the fault.
     */
    public XMLBeansFault(XmlObject fault)
    {
        super();
        this.fault = fault;
        
        XmlCursor cursor = fault.newCursor();
        cursor.toFirstChild();

        if (cursor.getName().getNamespaceURI().equals(SOAPConstants.SOAP11_ENVELOPE_NS))
            createSoap11Fault(cursor);
        else
            createSoap12Fault(cursor);
    }
    
    /**
     * Fill in the fault information from the XmlCursor.
     * @param cursor
     */
    protected void createSoap11Fault(XmlCursor cursor)
    {
        cursor.toFirstChild();
        
        do
        {
            if ( cursor.getName().getLocalPart().equals("faultcode") )
            {
                setFaultCode( cursor.getTextValue() );
            }
            if ( cursor.getName().getLocalPart().equals("faultstring") )
            {
                setMessage( cursor.getTextValue() );
            }
            if ( cursor.getName().getLocalPart().equals("faultactor") )
            {
                setRole( cursor.getTextValue() );
            }
            if ( cursor.getName().getLocalPart().equals("faultcode") )
            {
                setDetail( cursor.getDomNode() );
            }
        }
        while( cursor.toNextSibling() );
    }

    
    /**
     * Fill in the fault information from the XmlCursor.
     * @param cursor
     */
    protected void createSoap12Fault(XmlCursor cursor)
    {
        // TODO
    }
    
    /**
     * @return Returns the fault.
     */
    public XmlObject getFault()
    {
        return fault;
    }
    
    /**
     * @param fault The fault to set.
     */
    public void setFault(XmlObject fault)
    {
        this.fault = fault;
    }
}
