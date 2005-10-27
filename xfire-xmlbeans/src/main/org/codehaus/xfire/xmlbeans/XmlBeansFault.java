package org.codehaus.xfire.xmlbeans;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.codehaus.xfire.util.stax.FragmentStreamReader;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 17, 2004
 */
public class XmlBeansFault
    extends XFireFault
{
    private static final long serialVersionUID = 1L;
    
    private XmlObject fault;
    
    /**
     * @param fault The XmlObject representing the fault.
     */
    public XmlBeansFault(XmlObject fault)
    {
        super();
        this.fault = fault;
        
        XmlCursor cursor = fault.newCursor();
        cursor.toFirstChild();

        if (cursor.getName().getNamespaceURI().equals(Soap11.getInstance().getNamespace()))
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
            if ( cursor.getName().getLocalPart().equals("detail") )
            {
                StaxBuilder builder = new StaxBuilder();
                try
                {
                    setDetail( builder.build(new FragmentStreamReader(cursor.newXMLStreamReader())).getRootElement() );
                }
                catch (XMLStreamException e)
                {
                    e.printStackTrace();
                }
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
