package org.codehaus.xfire.attachments;

import java.net.URI;
import java.net.URISyntaxException;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.util.UID;

public class AttachmentUtil
{
    public static String createContentID(String ns)
    {
        String uid = UID.generate();
        try
        {
            URI uri = new URI(ns);
            return uid + "@" + uri;
        }
        catch (URISyntaxException e)
        {
            throw new XFireRuntimeException("Could not create URI for namespace: " + ns);
        }
    }
}
