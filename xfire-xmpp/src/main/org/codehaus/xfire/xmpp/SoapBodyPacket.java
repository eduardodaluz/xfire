package org.codehaus.xfire.xmpp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.dom4j.Document;
import org.jivesoftware.smack.packet.IQ;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SoapBodyPacket
    extends IQ
{
    private Document document;
    private String body;
    
    public SoapBodyPacket(String body)
    {
        this.body = body;
    }
    
    public SoapBodyPacket(Document document)
    {
        this.document = document;
    }
    
    public String getChildElementXML()
    {
        if (document != null)
            return document.asXML();
        else
            return body;
    }
   
    public Document getDocument()
    {
        return document;
    }
    
    public InputStream getDocumentInputStream()
    {
        return new ByteArrayInputStream( document.asXML().getBytes() );
    }
}
