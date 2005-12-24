package org.codehaus.xfire.util.dom;

import javax.xml.parsers.DocumentBuilderFactory;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.soap.handler.ReadHeadersHandler;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.util.stax.W3CDOMStreamReader;
import org.w3c.dom.Document;

/**
 * Reads the incoming stream to a DOM document and sets the stream to
 * a W3CDOMStreamReader.
 * 
 * @author Dan Diephouse
 */
public class DOMInHandler
    extends AbstractHandler
{
    public DOMInHandler()
    {
        super();
        before(ReadHeadersHandler.class.getName());
    }

    public String getPhase()
    {
        return Phase.PARSE;
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        doc = STAXUtils.read(dbf.newDocumentBuilder(), context.getInMessage().getXMLStreamReader());

        context.getInMessage().setXMLStreamReader(new W3CDOMStreamReader(doc.getDocumentElement()));
    }
}
