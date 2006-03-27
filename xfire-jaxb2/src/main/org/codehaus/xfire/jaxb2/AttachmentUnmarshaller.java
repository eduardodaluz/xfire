package org.codehaus.xfire.jaxb2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.attachments.Attachment;
import org.codehaus.xfire.attachments.AttachmentUtil;
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
        Attachment a = AttachmentUtil.getAttachment(cid, context.getOutMessage());
        if (a == null)
            throw new IllegalArgumentException("Attachment " + cid + " was not found.");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            copy(a.getDataHandler().getInputStream(), bos);
        }
        catch (IOException e)
        {
            throw new XFireRuntimeException("Could not read attachment.", e);
        }
        return bos.toByteArray();
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


    @Override
    public DataHandler getAttachmentAsDataHandler(String cid)
    {
        Attachment a = AttachmentUtil.getAttachment(cid, context.getInMessage());
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
