package org.codehaus.xfire.xmpp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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
        if (document != null)
            return document.getRootElement().asXML();
        else
            return body;
    }
   
    public Document getDocument()
    {
        return document;
    }
    
    public InputStream getDocumentInputStream()
    {
        return new ByteArrayInputStream( document.getRootElement().asXML().getBytes() );
    }
}
