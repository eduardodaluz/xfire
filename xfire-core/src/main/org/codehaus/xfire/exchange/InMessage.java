package org.codehaus.xfire.exchange;

import javax.xml.stream.XMLStreamReader;

/**
 * A "in" message. These arrive at endpoints.

 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class InMessage
    extends AbstractMessage
{
    private XMLStreamReader xmlStreamReader;

    public InMessage()
    {
    }
    
    public InMessage(XMLStreamReader xmlStreamReader)
    {
        this.xmlStreamReader = xmlStreamReader;
        setUri(ANONYMOUS_URI);
    }
    
    public InMessage(XMLStreamReader xmlStreamReader, String uri)
    {
        this.xmlStreamReader = xmlStreamReader;
        setUri(uri);
    }

    public XMLStreamReader getXMLStreamReader()
    {
        return xmlStreamReader;
    }
}