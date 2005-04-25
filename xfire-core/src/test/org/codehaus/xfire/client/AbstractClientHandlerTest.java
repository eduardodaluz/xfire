package org.codehaus.xfire.client;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import junit.framework.TestCase;

import org.codehaus.xfire.fault.XFireFault;

public class AbstractClientHandlerTest
        extends TestCase
{
    private AbstractClientHandler abstractClientHandler;

    protected void setUp()
            throws Exception
    {
        abstractClientHandler = new AbstractClientHandler()
        {
            public void writeRequest(XMLStreamWriter writer)
                    throws XMLStreamException
            {
            }

            public void handleResponse(XMLStreamReader reader)
                    throws XMLStreamException, XFireFault
            {
            }
        };
    }


    public void testHasRequest()
            throws Exception
    {
        assertTrue(abstractClientHandler.hasRequest());
    }

    public void testGetAttachments()
            throws Exception
    {
        assertNull(abstractClientHandler.getAttachments());
    }
}