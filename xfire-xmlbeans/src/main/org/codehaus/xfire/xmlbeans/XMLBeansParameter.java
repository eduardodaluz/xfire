package org.codehaus.xfire.xmlbeans;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.java.Parameter;
import org.codehaus.xfire.util.STAXUtils;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 16, 2004
 */
public class XMLBeansParameter
	extends Parameter
{
    public XMLBeansParameter(SchemaType type)
    {
        super(type.getDocumentElementName(), new XMLBeansType(type));
    }

    public Object read(XMLStreamReader reader)
        throws XFireFault
    {
        try
        {
            return XmlObject.Factory.parse(reader);
        }
        catch (XmlException e)
        {
            throw new XFireFault("Couldn't parse the response.", e, XFireFault.SENDER);
        }
    }
    
    public void write(Object value, XMLStreamWriter xmlWriter)
        throws XFireFault
    {
        try
        {
            STAXUtils.copy( ((XmlObject) value).newXMLStreamReader(), xmlWriter );
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Couldn't parse the response.", e, XFireFault.SENDER);
        }
    }
}
