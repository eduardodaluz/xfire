package org.codehaus.xfire.security.wssecurity;

import java.io.InputStream;

import junit.framework.TestCase;

import org.codehaus.xfire.security.InSecurityResult;
import org.codehaus.xfire.util.DOMUtils;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class WSS4jInSecurityProcessorTest
    extends TestCase
{

    public WSS4jInSecurityProcessorTest(String arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

//   public  void testProcessor()
//        throws Exception
//    {
//
//        InputStream inStream = getClass().getClassLoader()
//                .getResourceAsStream("META-INF/xfire/response.xml");
//        Document doc = DOMUtils.readXml(inStream);
//        WSS4JInSecurityProcessor processor = new WSS4JInSecurityProcessor();
//        Document decrypted = null;
//        InSecurityResult result = processor.process(doc);
//        decrypted = result.getDocument();
//        DOMUtils.writeXml(decrypted.getDocumentElement(), System.out);
//    }
//    
     public  void testUserNameTokenProcessor()
        throws Exception
    {

        InputStream inStream = getClass().getResourceAsStream("sample-wsse-request.xml");
        Document doc = DOMUtils.readXml(inStream);
        WSS4JInSecurityProcessor processor = new WSS4JInSecurityProcessor();
        Document decrypted = null;
        InSecurityResult result = processor.process(doc);
        assertEquals("cupareq",result.getUser());
        assertEquals("cupareq1",result.getPassword());        
        decrypted = result.getDocument();
        DOMUtils.writeXml(decrypted.getDocumentElement(), System.out);
    }
}
