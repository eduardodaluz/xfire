package org.codehaus.xfire.xmlbeans;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
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
        
    }
    
    public Element getDetailElement()
    {
        return null;
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
