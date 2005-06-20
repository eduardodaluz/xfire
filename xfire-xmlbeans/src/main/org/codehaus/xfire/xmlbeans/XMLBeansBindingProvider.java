package org.codehaus.xfire.xmlbeans;

import java.util.Iterator;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.MessageHeaderInfo;
import org.codehaus.xfire.service.MessagePartContainer;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.util.STAXUtils;

public class XMLBeansBindingProvider
    implements BindingProvider
{
    final static XmlOptions options = new XmlOptions();
    static
    {
        options.setSaveInner();
    }

    public void initialize(Service service)
    {
        for (Iterator itr = service.getServiceInfo().getOperations().iterator(); itr.hasNext();)
        {
            OperationInfo opInfo = (OperationInfo) itr.next();
            
            initializeMessage(service, opInfo.getInputMessage());
            initializeMessage(service, opInfo.getOutputMessage());
        }
    }

    protected void initializeMessage(Service service, MessagePartContainer container)
    {
        for (Iterator itr = container.getMessageParts().iterator(); itr.hasNext();)
        {
            MessagePartInfo part = (MessagePartInfo) itr.next();
            
            part.setSchemaType(new XMLBeansType());
        }
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

    public Object readHeader(MessageHeaderInfo p, MessageContext context)
        throws XFireFault
    {
        throw new UnsupportedOperationException("Headers are not yet supported with the XMLBeans binding.");
    }

    public void writeHeader(MessagePartInfo p, MessageContext context, Object value)
        throws XFireFault
    {
        throw new UnsupportedOperationException("Headers are not yet supported with the XMLBeans binding.");
    }

}
