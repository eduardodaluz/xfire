package org.codehaus.xfire.security.wssecurity;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.codehaus.xfire.security.SecurityActions;
import org.codehaus.xfire.security.SecurityResult;
import org.codehaus.xfire.security.impl.PropertiesLoader;
import org.codehaus.xfire.security.impl.SecurityProperties;
import org.codehaus.xfire.util.DOMUtils;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSS4jInSecurityProcessorTest extends TestCase {

	public WSS4jInSecurityProcessorTest(String arg0) {
		super(arg0);

	}

	/**
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private Document readDocument(String file) throws Exception {
		InputStream inStream = getClass().getResourceAsStream(file);

		Document doc = DOMUtils.readXml(inStream);

		inStream.close();
		return doc;
	}

	/**
	 * @throws Exception
	 */
	public void testUserNameTokenProcessor() throws Exception {
		Document doc = readDocument("requests\\sample-wsse-request.xml");
		WSS4JInSecurityProcessor processor = new WSS4JInSecurityProcessor();
		InSecurityDefaultBuilder builder = new InSecurityDefaultBuilder();
		Map config = new HashMap();
		config.put(SecurityProperties.PROP_ACTIONS,
				SecurityActions.AC_USERTOKEN);
		builder.setConfiguration(config);
		builder.build(processor);
		SecurityResult result = processor.process(doc);
		assertEquals("cupareq", result.getUser());
		assertEquals("cupareq1", result.getPassword());

	}

	/*
	 * public void testSignature() throws Exception { Document doc =
	 * readDocument("signout.xml");
	 * 
	 * WSS4JInSecurityProcessor processor = new WSS4JInSecurityProcessor();
	 * InSecurityDefaultBuilder builder = new InSecurityDefaultBuilder();
	 * builder.setConfiguration(new PropertiesLoader()
	 * .loadConfigFile("META-INF/xfire/insecurity_sign.properties"));
	 * SecurityResult result = processor.process(doc); }
	 */

	public void testEncryption() throws Exception {
		Document doc = readDocument("in_enc.xml");
		PropertiesLoader a = new PropertiesLoader();
		Map map = a.loadConfigFile("META-INF/xfire/insecurity_enc.properties");
		InSecurityDefaultBuilder builder = new InSecurityDefaultBuilder();
		builder.setConfiguration(map);
		WSS4JInSecurityProcessor processor = new WSS4JInSecurityProcessor();
		builder.build(processor);
		processor.process(doc);

	}

}
