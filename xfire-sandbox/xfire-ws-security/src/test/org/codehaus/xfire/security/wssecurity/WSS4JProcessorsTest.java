package org.codehaus.xfire.security.wssecurity;

import java.io.InputStream;

import org.codehaus.xfire.security.InSecurityProcessorBuilder;
import org.codehaus.xfire.security.InSecurityResult;
import org.codehaus.xfire.security.OutSecurityProcessorBuilder;
import org.codehaus.xfire.security.exceptions.WSSecurityException;
import org.codehaus.xfire.util.DOMUtils;
import org.w3c.dom.Document;

import junit.framework.TestCase;

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
    private void setupConfigFile(OutSecurityProcessorBuilder builder, String path)
    {
        WSS4JOutProcessorBuilder outBuilder = (WSS4JOutProcessorBuilder) builder;
        outBuilder.setConfigFile(path);
    }

    /**
     * @param builder
     * @param path
     */
    private void setupConfigFile(InSecurityProcessorBuilder builder, String path)
    {
        WSS4JInProcessorBuilder outBuilder = (WSS4JInProcessorBuilder) builder;
        outBuilder.setConfigFile(path);
    }

    /**
     * @param outConfig
     * @param inConfig
     * @return
     * @throws Exception
     */
    protected InSecurityResult processRequest(String outConfig, String inConfig)
        throws Exception
    {
        Document doc = readDocument("wsse-request-clean.xml");

        WSS4JOutSecurityProcessor outProcessor = new WSS4JOutSecurityProcessor();
        setupConfigFile(outProcessor.getBuilder(), outConfig);
        doc = outProcessor.process(doc);
        assertNotNull(doc);
        WSS4JInSecurityProcessor processor = new WSS4JInSecurityProcessor();
        setupConfigFile(processor.getBuilder(), inConfig);
        InSecurityResult result = processor.process(doc);
        return result;
    }

    /**
     * @throws Exception
     */
    public void testSignatureFull()
        throws Exception
    {
        InSecurityResult result = processRequest("META-INF/xfire/outsecurity_sign.properties",
                                                 "META-INF/xfire/insecurity_sign.properties");

    }

    /**
     * @throws Exception
     */
    public void testEncryptionFull()
        throws Exception
    {
        InSecurityResult result = processRequest("META-INF/xfire/outsecurity_enc.properties",
                                                 "META-INF/xfire/insecurity_enc.properties");

    }

    /**
     * @throws Exception
     */
    public void testUserTokenPlain()
        throws Exception
    {
        InSecurityResult result = processRequest("META-INF/xfire/outsecurity_ut.properties",
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
        InSecurityResult result = processRequest("META-INF/xfire/outsecurity_ut_dig.properties",
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

        InSecurityResult result = processRequest("META-INF/xfire/outsecurity_timestamp.properties",
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
        setupConfigFile(outProcessor.getBuilder(),
                        "META-INF/xfire/outsecurity_timestamp.properties");
        doc = outProcessor.process(doc);
        assertNotNull(doc);
        Thread.sleep(2000);
        WSS4JInSecurityProcessor processor = new WSS4JInSecurityProcessor();
        setupConfigFile(processor.getBuilder(), "META-INF/xfire/insecurity_timestamp.properties");
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

}
