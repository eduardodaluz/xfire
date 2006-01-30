package org.codehaus.xfire.security.wssecurity;

import java.io.InputStream;

import junit.framework.TestCase;

import org.codehaus.xfire.security.InSecurityProcessor;
import org.codehaus.xfire.security.SecurityProcessor;
import org.codehaus.xfire.security.SecurityResult;
import org.codehaus.xfire.security.exceptions.WSSecurityException;
import org.codehaus.xfire.security.impl.PropertiesLoader;
import org.codehaus.xfire.util.DOMUtils;
import org.w3c.dom.Document;

public class WSS4JProcessorsTest
    extends TestCase
{

    /**
     * @param file
     * @return
     * @throws Exception
     */
    private Document readDocument(String file)
        throws Exception
    {
        InputStream inStream = getClass().getResourceAsStream(file);
        Document doc = DOMUtils.readXml(inStream);
        inStream.close();
        return doc;
    }

    /**
     * @param builder
     * @param path
     */
    private void setupOutConfigFile(SecurityProcessor processor, String path)
    {

        OutSecurityDefaultBuilder outBuilder = new OutSecurityDefaultBuilder();
        outBuilder.setConfiguration(new PropertiesLoader().loadConfigFile(path));
        outBuilder.build(processor);
    }

    
    private void setupInConfigFile(SecurityProcessor processor, String path)
    {

        InSecurityDefaultBuilder outBuilder = new InSecurityDefaultBuilder();
        outBuilder.setConfiguration(new PropertiesLoader().loadConfigFile(path));
        outBuilder.build(processor);
    }
  

    /**
     * @param outConfig
     * @param inConfig
     * @return
     * @throws Exception
     */
    protected SecurityResult processRequest(String outConfig, String inConfig)
        throws Exception
    {
        Document doc = readDocument("wsse-request-clean.xml");

        WSS4JOutSecurityProcessor outProcessor = new WSS4JOutSecurityProcessor();
        setupOutConfigFile(outProcessor, outConfig);
        doc = outProcessor.process(doc).getDocument();
        assertNotNull(doc);
        WSS4JInSecurityProcessor processor = new WSS4JInSecurityProcessor();
        setupInConfigFile(processor, inConfig);
        SecurityResult result = processor.process(doc);
        return result;
    }

    /**
     * @throws Exception
     */
    public void testSignatureFull()
        throws Exception
    {
        SecurityResult result = processRequest("META-INF/xfire/outsecurity_sign.properties",
                                                 "META-INF/xfire/insecurity_sign.properties");

    }

    /**
     * @throws Exception
     */
    public void testEncryptionFull()
        throws Exception
    {
        SecurityResult result = processRequest("META-INF/xfire/outsecurity_enc.properties",
                                                 "META-INF/xfire/insecurity_enc.properties");

    }

    /**
     * @throws Exception
     */
    public void testUserTokenPlain()
        throws Exception
    {
        SecurityResult result = processRequest("META-INF/xfire/outsecurity_ut.properties",
                                                 "META-INF/xfire/insecurity_ut.properties");

        assertEquals(result.getUser(), "userName");
        assertEquals(result.getPassword(), "userSecretePass");
        assertTrue(!result.isPasswordHashed());
    }

    /**
     * @throws Exception
     */
    public void testUserTokenDigestedValid()
        throws Exception
    {
        SecurityResult result = processRequest("META-INF/xfire/outsecurity_ut_dig.properties",
                                                 "META-INF/xfire/insecurity_ut_dig.properties");
        assertEquals(result.getUser(), "userName");
        assertNotNull(result.getPassword());
        assertTrue(result.isPasswordHashed());
    }

    /**
     * @throws Exception
     */
    public void testTimestampValid()
        throws Exception
    {

        SecurityResult result = processRequest("META-INF/xfire/outsecurity_timestamp.properties",
                                                 "META-INF/xfire/insecurity_timestamp.properties");

        assertNotNull(result.getTsExpire());
    }

    /**
     * @throws Exception
     */
    public void testTimestampExpired()
        throws Exception
    {
        Document doc = readDocument("wsse-request-clean.xml");

        WSS4JOutSecurityProcessor outProcessor = new WSS4JOutSecurityProcessor();
        setupOutConfigFile(outProcessor, "META-INF/xfire/outsecurity_timestamp.properties");
        doc = outProcessor.process(doc).getDocument();
        assertNotNull(doc);
        Thread.sleep(2000);
        WSS4JInSecurityProcessor processor = new WSS4JInSecurityProcessor();
        setupInConfigFile(processor, "META-INF/xfire/insecurity_timestamp.properties");
        try
        {
            processor.process(doc);
        }
        catch (WSSecurityException ex)
        {
            return;
        }
        assertTrue("Exipire mechanism failed", false);
    }

    /**
     * @throws Exception
     */
    public void testEncryptTimestampUTValid()
        throws Exception
    {

        SecurityResult result = processRequest("META-INF/xfire/outsecurity_mix.properties",
                                                 "META-INF/xfire/insecurity_mix.properties");

        assertNotNull(result.getTsExpire());
        assertEquals(result.getUser(), "userName");
        assertNotNull(result.getPassword());
        assertTrue(result.isPasswordHashed());
    }
    
    public void testService(){
        assertTrue(true);
    }
}
