package org.codehaus.xfire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import junit.framework.TestCase;

public class MessageContextTest
        extends TestCase
{
    private MessageContext messageContext;

    protected void setUp()
            throws Exception
    {
        messageContext = new MessageContext();

    }

    public void testSetFaultDestination()
            throws Exception
    {
        MessageDestination destination = new MessageDestination(new ByteArrayOutputStream(), "url");
        messageContext.setFaultDestination(destination);
        assertEquals("Invalid fault destination", destination, messageContext.getFaultDestination());
    }

    public void testSetReplyDestination()
            throws Exception
    {
        MessageDestination destination = new MessageDestination(new ByteArrayOutputStream(), "url");
        messageContext.setReplyDestination(destination);
        assertEquals("Invalid reply destination", destination, messageContext.getReplyDestination());
    }

    public void testGetRequestUri()
            throws Exception
    {
        String requestUri = "uri";
        messageContext.setRequestUri(requestUri);
        assertEquals("Invalid request uri", requestUri, messageContext.getRequestUri());
    }

    public void testSetRequestStream()
            throws Exception
    {
        byte[] buf = new byte[]{0x1, 0x2};
        InputStream stream = new ByteArrayInputStream(buf);
        messageContext.setRequestStream(stream);
    }
}