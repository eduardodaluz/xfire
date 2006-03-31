package org.codehaus.xfire.aegis.type.mtom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.attachments.Attachment;
import org.codehaus.xfire.attachments.ByteDataSource;
import org.codehaus.xfire.attachments.SimpleAttachment;
import org.codehaus.xfire.soap.SoapConstants;

/**
 * @author Dan Diephouse
 */
public class ByteArrayType extends AbstractXOPType
{
    public ByteArrayType()
    {
        setTypeClass(byte[].class);
        setSchemaType(new QName(SoapConstants.XSD, "base64Binary"));
    }
    
    protected Object readAttachment(Attachment att, MessageContext context) throws IOException
    {
        DataHandler handler = att.getDataHandler();
        InputStream is = handler.getInputStream();
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(is, out);
        
        return out.toByteArray();
    }
    
    public static void copy(InputStream input, OutputStream output) throws IOException
    {
        try
        {
            final byte[] buffer = new byte[8096];

            int n = 0;
            while (-1 != (n = input.read(buffer)))
            {
                output.write(buffer, 0, n);
            }
        }
        finally
        {
            output.close();
            input.close();
        }
    }
    
    protected Attachment createAttachment(Object object, String id)
    {
        byte[] data = (byte[]) object;
        
        ByteDataSource source = new ByteDataSource(data);
        source.setContentType(getContentType(object, null));
        SimpleAttachment att = new SimpleAttachment(id, new DataHandler(source));
        att.setXOP(true);
        
        return att;
    }
    
    protected String getContentType(Object object, MessageContext context)
    {
        return "application/octet-stream";
    }
}
