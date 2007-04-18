package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.ws.security.handler.WSHandlerConstants;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * Encryption client with encrypted config file
 * 
 */
public class SecuredBookClientEnc extends BookClient {

	public static void main(String[] args) throws MalformedURLException {
		new SecuredBookClientEnc().executeClient("BookServiceENC");
	}

	protected void configureOutProperties(Properties properties) {
		properties.setProperty(WSHandlerConstants.ACTION,
				WSHandlerConstants.ENCRYPT);
		properties.setProperty(WSHandlerConstants.USER, "serveralias");
		// Configuration of public key used to encrypt message goes to
		// properties file.
		properties.setProperty(WSHandlerConstants.ENC_PROP_FILE,
				"org/codehaus/xfire/client/sec_outsercurity_enc.properties");
	}
	protected String getName() {
		return "Secured Encryption Client";
	}

}
