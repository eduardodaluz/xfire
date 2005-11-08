package org.codehaus.xfire.security.impl;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class SecurityFileConfigurer {

	private static final Log LOG = LogFactory.getLog(SecurityFileConfigurer.class);

	public static final String PROP_KEYSTORE_TYPE = "xfire.security.keystore.type";

	public static final String PROP_KEYSTORE_PASS = "xfire.security.keystore.password";

	public static final String PROP_KEYSTORE_FILE = "xfire.security.keystore.file";

	public static final String PROP_KEY_ALIAS = "xfire.security.key.alias";

	public static final String PROP_KEY_PASS = "xfire.security.key.password";

	public static final String PROP_CERT_FILE = "xfire.security.cert.file";

	public static final String PROP_ENC_ALG = "xfire.security.encrypt.key.algoritm";

	public static final String PROP_SYM_ALG = "xfire.security.symmetric.key.algoritm";

	
	/**
	 * @param path
	 * @return
	 */
	protected Properties loadConfigFile(String path) {

		Properties props = new Properties();
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(path);
		if (inStream == null) {
			throw new RuntimeException("Security config file not found :" + path);
		}

		try {
			props.load(inStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {

				}
			}
		}

		return props;
	}

	/**
	 * @param inStream
	 * @param keyStoreType
	 * @param keyStorePassword
	 */
	protected KeyStore loadKeyStore(InputStream inStream, String keyStoreType, String keyStorePassword) {
		try {
			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(inStream, keyStorePassword.toCharArray());
			return keyStore;
		} catch (Exception e) {
			LOG.error(e);
			throw new RuntimeException("Unable to load keyStore: ", e);
		}

	}
	
	

	/**
	 * @param keystore
	 * @param alias
	 * @param pass
	 * @return
	 */
	protected PrivateKey getPrivateKey(KeyStore keystore, String alias, String pass) throws Exception {
		return (PrivateKey) keystore.getKey(alias, pass.toCharArray());
	}

	/**
	 * @param keystore
	 * @param alias
	 * @return
	 * @throws Exception
	 */
	protected Certificate getCertificate(KeyStore keystore, String alias) throws Exception {
		return keystore.getCertificate(alias);
	}
}
