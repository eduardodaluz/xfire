package org.codehaus.xfire.xmlbeans;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.java.message.MessageReader;
import org.codehaus.xfire.java.message.MessageWriter;
import org.codehaus.xfire.java.type.Type;
import org.codehaus.xfire.util.STAXUtils;
import org.dom4j.Element;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 13, 2004
 */
public class XMLBeansType
	extends Type
{
    private SchemaType schemaType;

    public XMLBeansType(SchemaType schemaType)
    {
        this.schemaType = schemaType;
    }

    public Object readObject(MessageReader reader) 
    	throws XFireFault
    {
        try
        {
            return XmlObject.Factory.parse(reader.getXMLStreamReader());
        }
        catch( XmlException e )
        {
            throw new XFireFault("Could not read request.", e, XFireFault.SENDER);
        }
    }

    public void writeObject(Object o, MessageWriter writer)
    	throws XFireFault
    {
        try
        {
            XmlObject obj = (XmlObject) o; 

            STAXUtils.copy(obj.newXMLStreamReader(), 
                           writer.getXMLStreamWriter());
            
            writer.close();
        } 
        catch (XMLStreamException e)
        {
            throw new XFireFault("Could not write response.", e, XFireFault.SENDER);
        }
    }

    public void writeSchema(Element element)
    {
        // todo:
    }
    
    public boolean isComplex()
    {
        return true;
    }
    
    /**
     * @return Returns the schemaType.
     */
    public QName getSchemaType()
    {
        return schemaType.getDocumentElementName();
    }
}
