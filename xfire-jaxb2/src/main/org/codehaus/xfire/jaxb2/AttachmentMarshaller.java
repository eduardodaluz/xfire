package org.codehaus.xfire.jaxb2;

import javax.activation.DataHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.attachments.AttachmentUtil;
import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.attachments.ByteDataSource;
import org.codehaus.xfire.attachments.JavaMailAttachments;
import org.codehaus.xfire.attachments.SimpleAttachment;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.util.UID;

public class AttachmentMarshaller
    extends javax.xml.bind.attachment.AttachmentMarshaller
{
    private static final Log log = LogFactory.getLog(AttachmentMarshaller.class);
    private MessageContext context;

    public AttachmentMarshaller(MessageContext context)
    {
        super();
        this.context = context;
    }

    @Override
    public String addMtomAttachment(byte[] data,
                                    int offset,
                                    int length,
                                    String mimeType,
                                    String elementNamespace,
                                    String elementLocalName)
    {
        log.debug("Adding attachment {" + elementNamespace + "}" + elementLocalName);
        
        Attachments atts = context.getOutMessage().getAttachments();
        if (atts == null)
        {
            atts = new JavaMailAttachments();
            context.getOutMessage().setAttachments(atts);
        }
        
        ByteDataSource source = new ByteDataSource(data);
        source.setContentType(mimeType);
        DataHandler handler = new DataHandler(source);
        
        String id = AttachmentUtil.createContentID(elementNamespace);
        SimpleAttachment att = new SimpleAttachment(id, handler);
        att.setXOP(true);
        atts.addPart(att);
        return id;
    }

    @Override
    public String addMtomAttachment(DataHandler handler, String elementNS, String elementLocalName)
    {
        log.debug("Adding attachment {" + elementNS + "}" + elementLocalName);
        
        Attachments atts = context.getOutMessage().getAttachments();
        if (atts == null)
        {
            atts = new JavaMailAttachments();
            context.getOutMessage().setAttachments(atts);
        }

        String id = AttachmentUtil.createContentID(elementNS);
        SimpleAttachment att = new SimpleAttachment(id, handler);
        atts.addPart(att);
        att.setXOP(true);
        return id;
    }

    @Override
    public String addSwaRefAttachment(DataHandler handler)
    {
        Attachments atts = context.getOutMessage().getAttachments();
        if (atts == null)
        {
            atts = new JavaMailAttachments();
            context.getOutMessage().setAttachments(atts);
        }

        String id = UID.generate() + "@" + handler.getName();
        SimpleAttachment att = new SimpleAttachment(id, handler);
        atts.addPart(att);
        
        return id;
    }

    @Override
    public boolean isXOPPackage()
    {
        String mtomEnabled = (String) context.getContextualProperty(SoapConstants.MTOM_ENABLED);
        if (mtomEnabled == null) return false;
        
        return Boolean.valueOf(mtomEnabled).booleanValue();
    }

}
