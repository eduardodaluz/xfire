package org.codehaus.xfire.jaxb2;

import javax.activation.DataHandler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.attachments.Attachment;
import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.soap.SoapConstants;

public class AttachmentUnmarshaller
    extends javax.xml.bind.attachment.AttachmentUnmarshaller
{
    private MessageContext context;

    public AttachmentUnmarshaller(MessageContext context)
    {
        this.context = context;
    }

    @Override
    public byte[] getAttachmentAsByteArray(String cid)
    {
        Attachment a = getAttachment(cid);
        if (a == null)
            throw new IllegalArgumentException("Attachment " + cid + " was not found.");

        // return a.getDataHandler().;
        return null;
    }

    private Attachment getAttachment(String cid)
    {
        Attachments atts = context.getInMessage().getAttachments();
        if (atts == null)
            return null;

        return atts.getPart(cid);
    }

    @Override
    public DataHandler getAttachmentAsDataHandler(String cid)
    {
        Attachment a = getAttachment(cid);
        if (a == null)
            throw new IllegalArgumentException("Attachment " + cid + " was not found.");

        return a.getDataHandler();
    }

    @Override
    public boolean isXOPPackage()
    {
        String mtomEnabled = (String) context.getContextualProperty(SoapConstants.MTOM_ENABLED);
        if (mtomEnabled == null) return false;
        
        return Boolean.valueOf(mtomEnabled).booleanValue();
    }
}
