package org.codehaus.xfire.security.wssecurity;

import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.codehaus.xfire.security.SecurityResult;
import org.codehaus.xfire.security.impl.PropertiesLoader;
import org.codehaus.xfire.util.DOMUtils;
import org.codehaus.xfire.util.STAXUtils;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSS4jInSecurityProcessorTest extends TestCase {

    public WSS4jInSecurityProcessorTest(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public void testProcessor() throws Exception {
        Document doc = readDocument("encrypted_response.xml");
        WSS4JInSecurityProcessor processor = new WSS4JInSecurityProcessor();
        SecurityResult result = processor.process(doc);
        Document decrypted = result.getDocument();
        DOMUtils.writeXml(decrypted.getDocumentElement(), System.out);
    }

    private Document readDocument(String file) throws Exception {
        InputStream inStream = getClass().getResourceAsStream(file);

        Document doc = DOMUtils.readXml(inStream);

        inStream.close();

        return doc;
    }

    public void testUserNameTokenProcessor() throws Exception {
        Document doc = readDocument("sample-wsse-request");

        WSS4JInSecurityProcessor processor = new WSS4JInSecurityProcessor();
        Document decrypted = null;
        SecurityResult result = processor.process(doc);
        assertEquals("cupareq", result.getUser());
        assertEquals("cupareq1", result.getPassword());
        decrypted = result.getDocument();
        DOMUtils.writeXml(decrypted.getDocumentElement(), System.out);
    }

    public void testSignature() throws Exception {
        Document doc = readDocument("signout.xml");

        // WSS4JOutSecurityProcessor outProcessor = new
        // WSS4JOutSecurityProcessor ();

        // doc = outProcessor.process(doc);
        DOMUtils utils = new DOMUtils();
        // utils.writeXml(doc, System.out);
        WSS4JInSecurityProcessor processor = new WSS4JInSecurityProcessor();
        InSecurityDefaultBuilder builder = new InSecurityDefaultBuilder();
        builder.setConfiguration(new PropertiesLoader()
                .loadConfigFile("META-INF/xfire/insecurity_sign.properties"));
        SecurityResult result = processor.process(doc);
        int z = 0;
    }

    public void testEncryption() throws Exception {
        Document doc = readDocument("tcpmon.xml");
        PropertiesLoader a = new PropertiesLoader();
        Map map = a.loadConfigFile("META-INF/xfire/insecurity.properties");
        WSS4JOutSecurityProcessor outProcessor = new WSS4JOutSecurityProcessor();
        SecurityResult result = outProcessor.process(doc);
        doc = result.getDocument();
        DOMUtils utils = new DOMUtils();
        utils.writeXml(doc, System.out);

        InSecurityDefaultBuilder builder = new InSecurityDefaultBuilder();
        builder.setConfiguration(map);

        WSS4JInSecurityProcessor processor = new WSS4JInSecurityProcessor();
        builder.build(processor);
        result = processor.process(doc);
        int z = 0;
        DOMUtils.writeXml(result.getDocument(), System.out);
    }

}
