package org.codehaus.xfire.xmlbeans;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.wsdl.SchemaType;

public class XMLBeansBindingProvider
    implements BindingProvider
{
    final static XmlOptions options = new XmlOptions();
    static
    {
        options.setSaveInner();
    }

    public void initialize(Service newParam)
    {
    }


    public Object readParameter(MessagePartInfo p, XMLStreamReader reader, MessageContext context)
        throws XFireFault
    {
        try
        {
            return XmlObject.Factory.parse(reader);
        }
        catch( XmlException e )
        {
            throw new XFireFault("Could not read request.", e, XFireFault.SENDER);
        }
    }

    public void writeParameter(MessagePartInfo p, XMLStreamWriter writer, MessageContext context, Object value)
        throws XFireFault
    {
        try
        {
            XmlObject obj = (XmlObject) value; 

            STAXUtils.copy(obj.newXMLStreamReader(), 
                           writer);
            
            /*XmlCursor cursor = obj.newCursor();
            if (cursor.toFirstChild() && cursor.toFirstChild())
            {
                do
                {
                    STAXUtils.copy(cursor.newXMLStreamReader(), 
                                   (XMLStreamWriter) context.getProperty(AbstractHandler.STAX_WRITER_KEY));
                }
                while(cursor.toNextSibling());
            }*/
        } 
        catch (XMLStreamException e)
        {
            throw new XFireFault("Could not write response.", e, XFireFault.SENDER);
        }
    }

    public SchemaType getSchemaType(Service service, MessagePartInfo param)
    {
        return new XMLBeansType();
    }
}
