package org.codehaus.xfire.attachments;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * Manages attachments for an invocation.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface Attachments
{
    /** The key for the Attachments object in the MessageContext. */
    String ATTACHMENTS_KEY = "xfire.attachments";

    /**
     * @return Returns the SOAP Message.
     */
    Attachment getSoapMessage();

    /**
     * @param soapMessage The SOAP Message to set.
     */
    void setSoapMessage(Attachment soapMessage);

    void addPart(Attachment part);

    Iterator getParts();

    Attachment getPart(String id);

    int size();

    void write(OutputStream out) throws IOException;

    String getContentType();
}