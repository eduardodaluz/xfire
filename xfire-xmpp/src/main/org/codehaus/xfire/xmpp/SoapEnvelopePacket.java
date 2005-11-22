package org.codehaus.xfire.xmpp;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.jivesoftware.smack.packet.IQ;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SoapEnvelopePacket
    extends IQ
{
    private String body;
    private Document doc;
    private final static XMLOutputter outputter = new XMLOutputter();
    
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
        if (body != null)
        {
            return body;
        }
        else if (doc != null)
        {
            return outputter.outputString(doc);
        }
        else
        {
            throw new RuntimeException("Body can not be null!");
        }
    }
    
    public Document getDocument()
    {
        return doc;
    }
}
