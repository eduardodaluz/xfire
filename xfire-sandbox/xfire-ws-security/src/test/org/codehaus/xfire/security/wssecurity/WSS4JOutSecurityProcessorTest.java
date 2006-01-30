package org.codehaus.xfire.security.wssecurity;

import java.io.InputStream;

import junit.framework.TestCase;

import org.codehaus.xfire.util.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSS4JOutSecurityProcessorTest
    extends TestCase
{

    public WSS4JOutSecurityProcessorTest(String arg0)
    {
        super(arg0);

    }

    public void testProcessor()
        throws Exception
    {
        InputStream inStream = getClass().getClassLoader()
                .getResourceAsStream("META-INF/xfire/request.xml");
        Document doc = DOMUtils.readXml(inStream);
        WSS4JOutSecurityProcessor processor = new WSS4JOutSecurityProcessor();

        Document encrypted = processor.process(doc).getDocument();
        NodeList list = encrypted
                .getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
                                        "Security");
        assertTrue(list.getLength() > 0);
        DOMUtils.writeXml(encrypted.getDocumentElement(), System.out);

    }

    public void testProcessorUserToken()
        throws Exception
    {
        InputStream inStream = getClass().getClassLoader()
                .getResourceAsStream("META-INF/xfire/request.xml");
        Document doc = DOMUtils.readXml(inStream);
        WSS4JOutSecurityProcessor processor = new WSS4JOutSecurityProcessor();
        processor.setUsername("userName");
        processor.setUserPassword("userNamePass");
        Document encrypted = processor.process(doc).getDocument();
        NodeList list = encrypted
                .getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
                                        "Security");
        assertTrue(list.getLength() > 0);
        DOMUtils.writeXml(encrypted.getDocumentElement(), System.out);

    }
}
