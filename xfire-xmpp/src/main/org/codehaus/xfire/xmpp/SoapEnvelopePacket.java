package org.codehaus.xfire.xmpp;

import org.jdom.Document;
import org.jivesoftware.smack.packet.IQ;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SoapEnvelopePacket
    extends IQ
{
    private String body;
    private Document doc;
    
    public SoapEnvelopePacket(String body)
    {
        this.body = body;
    }

    public SoapEnvelopePacket(Document doc)
    {
        this.doc = doc;
    }
    
    public String getChildElementXML()
    {
        if (body == null) throw new RuntimeException();
        return body;
    }
    
    public Document getDocument()
    {
        return doc;
    }
}
