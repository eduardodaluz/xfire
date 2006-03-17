package org.codehaus.xfire.aegis.type.mtom;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.activation.DataContentHandler;
import javax.activation.DataHandler;
import javax.activation.URLDataSource;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.attachments.Attachment;
import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.attachments.DefaultDataContentHandlerFactory;
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
 
    private static DefaultDataContentHandlerFactory factory =
        new DefaultDataContentHandlerFactory();
    
    private Map classToCType;
    private Map CTypeToClass;
    
    public XOPType()
    {
    }
    
    public Object readObject(MessageReader reader, MessageContext context)
    	throws XFireFault
    {
        
        String uri = null; //getURI(reader.getValue(null, "href"));
        String contentType = null; //reader.getValue(XML_MIME_NS, "contentType");
        
        Attachment att = getAttachment( uri, context );
        
        String type = att.getDataHandler().getContentType();
        DataContentHandler handler = factory.createDataContentHandler(type);
        try
        {
            if ( handler != null )
            {
                return handler.getContent(att.getDataHandler().getDataSource());
            }
            else
            {
                return att.getDataHandler().getContent();
            }
        }
        catch (IOException e)
        {
            throw new XFireFault("Could not read the attachment " + uri, e, XFireFault.SENDER);
        }
    }

    /**
     * Parse the URI from the <code>xop:Include</code> href value.
     * @param value
     * @return
     */
    protected String getURI(String value)
    {
        int index = value.indexOf(":");
        return value.substring(index+1);
    }

    public Attachment getAttachment(String id, MessageContext context) 
    	throws XFireFault
    {
        Attachments attachments = 
            (Attachments) context.getProperty(Attachments.ATTACHMENTS_KEY);
        Attachment att = null;
        
        if ( attachments != null)
        {
           att = attachments.getPart(id);
        }

        // Try loading the URL remotely
        try
        {
            URLDataSource source = new URLDataSource(new URL(id));
            att = new SimpleAttachment(id, new DataHandler(source));
        }
        catch (MalformedURLException e)
        {
            throw new XFireFault("Invalid attachment id: " + id, e, XFireFault.SENDER);
        }
        
        return att;
    }
    
    public void writeObject(Object object, MessageWriter writer, MessageContext context) 
    	throws XFireFault
    {
        /*Attachments attachments = 
            (Attachments) context.getProperty(Attachments.ATTACHMENTS_KEY);
        if ( attachments == null )
        {
            attachments = new JavaMailAttachments();
            context.setProperty(Attachments.ATTACHMENTS_KEY, attachments);
        }

        String id = null; // how do we generate this?
        String contentType = factory.getContentType(object.getClass()); // how?
        
        DataHandler handler = new DataHandler(object, contentType);
        Attachment att = new SimpleAttachment(id, handler);
        
        attachments.addPart(att);
          
        // write XOP element*/
        
        throw new UnsupportedOperationException();
    }
    
    
}
