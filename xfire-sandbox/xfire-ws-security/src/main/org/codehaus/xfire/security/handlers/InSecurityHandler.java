package org.codehaus.xfire.security.handlers;

import javax.xml.parsers.DocumentBuilderFactory;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.security.InSecurityProcessor;
import org.codehaus.xfire.security.InSecurityResult;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.util.stax.W3CDOMStreamReader;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class InSecurityHandler
    extends AbstractHandler
{

    private InSecurityProcessor processor;

    public void invoke(MessageContext context)
        throws Exception
    {

        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        doc = STAXUtils.read(dbf.newDocumentBuilder(), context.getInMessage().getXMLStreamReader());
        InSecurityResult result = getProcessor().process(doc);
        doc = result.getDocument();
        context.getInMessage().setXMLStreamReader(new W3CDOMStreamReader(doc.getDocumentElement()));

    }

    public InSecurityProcessor getProcessor()
    {
        return processor;
    }

    public void setProcessor(InSecurityProcessor processor)
    {
        this.processor = processor;
    }

}
