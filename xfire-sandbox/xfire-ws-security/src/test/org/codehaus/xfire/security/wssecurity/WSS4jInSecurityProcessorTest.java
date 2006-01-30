package org.codehaus.xfire.security.wssecurity;

import java.io.InputStream;
import java.util.Map;

import junit.framework.TestCase;

import org.codehaus.xfire.security.SecurityResult;
import org.codehaus.xfire.security.impl.PropertiesLoader;
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
    private Document readDocument(String file) throws Exception{
        InputStream inStream = getClass().getResourceAsStream(file);
        
        DOMUtils utils = new DOMUtils ();
        Document doc = utils.readXml2(inStream);
        /*Document doc = STAXUtils.read(DocumentBuilderFactory.newInstance().newDocumentBuilder(),
                                      STAXUtils.createXMLStreamReader(inStream, null),
                                      false);*/
        inStream.close();
        
        return doc;
    }
     /*public  void testUserNameTokenProcessor()
        throws Exception
    {

        InputStream inStream = getClass().getResourceAsStream("sample-wsse-request.xml");
        Document doc = STAXUtils.read(DocumentBuilderFactory.newInstance().newDocumentBuilder(),
                                      STAXUtils.createXMLStreamReader(inStream, null),
                                      false);

        WSS4JInSecurityProcessor processor = new WSS4JInSecurityProcessor();
        Document decrypted = null;
        InSecurityResult result = processor.process(doc);
        assertEquals("cupareq",result.getUser());
        assertEquals("cupareq1",result.getPassword());        
        decrypted = result.getDocument();
        DOMUtils.writeXml(decrypted.getDocumentElement(), System.out);
    }
*/     
     /*public void testSignature() throws Exception{
         Document doc = readDocument("wsse-request-sign.xml");
         
         WSS4JOutSecurityProcessor outProcessor = new WSS4JOutSecurityProcessor ();
         doc = outProcessor.process(doc);
         DOMUtils utils  = new DOMUtils ();
         utils.writeXml(doc, System.out);
         WSS4JInSecurityProcessor processor = new WSS4JInSecurityProcessor();
         InSecurityResult result = processor.process(doc);
         int z=0; 
     }*/
     
     public void testEncryption() throws Exception{
         Document doc = readDocument("tcpmon.xml");
         PropertiesLoader a  = new PropertiesLoader ();
         Map map = a.loadConfigFile("META-INF/xfire/insecurity.properties");
         /*WSS4JOutSecurityProcessor outProcessor = new WSS4JOutSecurityProcessor ();
         doc = outProcessor.process(doc);
         DOMUtils utils  = new DOMUtils ();
         utils.writeXml(doc, System.out);*/
         
         InSecurityDefaultBuilder builder = new InSecurityDefaultBuilder ();
         builder.setConfiguration(map);
         
         WSS4JInSecurityProcessor
         processor = new WSS4JInSecurityProcessor();
         builder.build(processor);
         SecurityResult result = processor.process(doc);
         int z=0; 
         DOMUtils.writeXml(result.getDocument(), System.out);
     }
     
}
