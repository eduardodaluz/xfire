package org.codehaus.xfire.attachments;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.codehaus.xfire.XFireRuntimeException;

/**
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class JavaMailAttachments 
	implements Attachments
{
    private static final String[] filter = new String[]{"Message-ID", "Mime-Version", "Content-Type"};
    
    private Map parts;
    
    private Attachment soapMessage;
    
    private MimeMultipart mimeMP;
    
    public JavaMailAttachments()
    {
        parts = new HashMap();
    }
    
    /**
     * Create Attachments from the MimeMultipart message.
     * 
     * @param multipart
     * @throws MessagingException
     */
    public JavaMailAttachments(MimeMultipart multipart) 
    	throws MessagingException
    {
        this();
        
        this.mimeMP = multipart;
        
        MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(0);
        setSoapMessage(new SimpleAttachment(part.getContentID(), part.getDataHandler()));
        
        for ( int i = 1; i < multipart.getCount(); i++ )
        {
            part = (MimeBodyPart) multipart.getBodyPart(i);
            addPart(new SimpleAttachment(part.getContentID(), part.getDataHandler()));
        }
    }
    
    /**
     * @return Returns the soapMessage.
     */
    public Attachment getSoapMessage()
    {
        return soapMessage;
    }
    
    /**
     * @param soapMessage The soapMessage to set.
     */
    public void setSoapMessage(Attachment soapMessage)
    {
        this.soapMessage = soapMessage;
    }
    
    public void addPart(Attachment part)
    {
        parts.put(part.getId(), part);
    }
    
    public Iterator getParts()
    {
        return parts.values().iterator();
    }
    
    public Attachment getPart(String id)
    {
        return (Attachment) parts.get(id);
    }

    public int size()
    {
        return parts.size();
    }
    
    public void write(OutputStream out) 
    	throws IOException
    {
        Session session = Session.getDefaultInstance(new Properties(), null);
        MimeMessage message = new MimeMessage(session);
        
        try
        {
            message.setContent(getMimeMultipart());
	        message.writeTo(out, filter);
        }
        catch( MessagingException e )
        {
            throw new XFireRuntimeException("Couldn't create message.", e);
        }
    }
    
    public MimeMultipart getMimeMultipart()
    {
        if ( mimeMP == null )
        {
            mimeMP = new MimeMultipart("related; type=\"text/xml\";start=\"<"
                    + getSoapMessage().getId() + ">\"");
           
            try
            {
                MimeBodyPart soapPart = new MimeBodyPart();
                soapPart.setDataHandler(soapMessage.getDataHandler());
                soapPart.setContentID("<"+soapMessage.getId()+">");
                mimeMP.addBodyPart(soapPart);
                
                for (Iterator itr = getParts(); itr.hasNext(); )
    	        {
    	            Attachment att = (Attachment) itr.next();
    	            
    	            MimeBodyPart part = new MimeBodyPart();
    	            part.setDataHandler(att.getDataHandler());
    	            part.setContentID("<"+att.getId()+">");
    	            mimeMP.addBodyPart(part);
    	        }
            }
            catch( MessagingException e )
            {
                throw new XFireRuntimeException("Couldn't create message.", e);
            }
        }

        return mimeMP;
    }
    
    public String getContentType()
    {
        return getMimeMultipart().getContentType();
    }
}
