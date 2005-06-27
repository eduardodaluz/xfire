package org.codehaus.xfire.xmpp;

import org.dom4j.Document;
import org.jivesoftware.smack.packet.IQ;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SoapEnvelopePacket
    extends IQ
{
    private Document document;
    private String body;
    
    public SoapEnvelopePacket(String body)
    {
        this.body = body;
    }
    
    public SoapEnvelopePacket(Document document)
    {
        this.document = document;
    }

    public String getChildElementXML()
    {
        return body;
    }
}
