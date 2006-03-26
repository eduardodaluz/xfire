package org.codehaus.xfire.aegis.type.mtom;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.attachments.Attachment;
import org.codehaus.xfire.attachments.AttachmentUtil;
import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.attachments.JavaMailAttachments;
import org.codehaus.xfire.attachments.SimpleAttachment;
import org.codehaus.xfire.fault.XFireFault;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public abstract class XOPType
	extends Type
{
    public final static String XOP_NS = "http://www.w3.org/2004/08/xop/include";
    public final static String XML_MIME_NS = "http://www.w3.org/2004/11/xmlmime";
 
    private final static QName XOP_INCLUDE = new QName(XOP_NS, "Include");
    private final static QName XOP_HREF = new QName("href");
    private final static QName XML_MIME_TYPE = new QName(XML_MIME_NS, "mimeType");
    
    public XOPType()
    {
    }
    
    public Object readObject(MessageReader reader, MessageContext context)
    	throws XFireFault
    {
        Object o = null;
        while (reader.hasMoreElementReaders())
        {
            MessageReader child = reader.getNextElementReader();
            if (child.getName().equals(XOP_INCLUDE))
            {
                MessageReader mimeReader = reader.getAttributeReader(XOP_HREF);
                String type = mimeReader.getValue();
                o = readInclude(type, reader, context);
            }
        }
        
        return o;
    }
    
    public Object readInclude(String type, MessageReader reader, MessageContext context)
        throws XFireFault
    {
        String href = reader.getAttributeReader(XOP_HREF).getValue();
        
        Attachment att = AttachmentUtil.getAttachment(href, context.getInMessage());
        
        if (att == null)
        {
            throw new XFireFault("Could not find the attachment " + href, XFireFault.SENDER);
        }
        
        return att.getDataHandler().getDataSource();
    }
    
    public void writeObject(Object object, MessageWriter writer, MessageContext context) 
    	throws XFireFault
    {
        Attachments attachments = context.getOutMessage().getAttachments();
        if ( attachments == null )
        {
            attachments = new JavaMailAttachments();
            context.getOutMessage().setAttachments(attachments);
        }

        DataSource source = (DataSource) object;
        String id = AttachmentUtil.createContentID(getSchemaType().getNamespaceURI());
        String contentType = source.getContentType();
        
        DataHandler handler = new DataHandler(source);
        SimpleAttachment att = new SimpleAttachment(id, handler);
        att.setXOP(true);
        attachments.addPart(att);
          
        MessageWriter mt = writer.getAttributeWriter(XML_MIME_TYPE);
        mt.writeValue(contentType);
        
        MessageWriter include = writer.getElementWriter(XOP_INCLUDE);
        MessageWriter href = include.getAttributeWriter(XOP_HREF);
        href.writeValue(id);
    }
}
