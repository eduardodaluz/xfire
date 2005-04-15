package org.codehaus.xfire.client;

/**
 * @author Arjen Poutsma
 */

import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

public class NullRequestHandlerTest
        extends TestCase
{
    private NullRequestHandler nullRequestHandler;

    protected void setUp()
            throws Exception
    {
        nullRequestHandler = new NullRequestHandler()
        {
            public void handleResponse(XMLStreamReader reader)

            {
            }
        };
    }

    public void testWriteRequest()
            throws Exception
    {
        nullRequestHandler.writeRequest(null);
    }

    public void testGetAttachments()
            throws Exception
    {
        assertNull(nullRequestHandler.getAttachments());
    }
}