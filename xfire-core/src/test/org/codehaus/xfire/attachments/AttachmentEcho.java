package org.codehaus.xfire.attachments;

import org.codehaus.xfire.MessageContext;
import org.jdom.Element;

public class AttachmentEcho
{
    public Element echo(Element echo, MessageContext context)
    {
        JavaMailAttachments atts = new JavaMailAttachments();
        Attachments recvd = context.getInMessage().getAttachments();
        
        Attachment att = (Attachment) context.getInMessage().getAttachments().getParts().next();
        att = new SimpleAttachment("test.jpg", att.getDataHandler());
        
        atts.addPart(att);
        context.getOutMessage().setAttachments(atts);
        
        return echo;
    }
}
