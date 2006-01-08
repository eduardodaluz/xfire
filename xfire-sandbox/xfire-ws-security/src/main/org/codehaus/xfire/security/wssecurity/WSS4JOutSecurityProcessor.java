package org.codehaus.xfire.security.wssecurity;

import java.util.Vector;

import org.apache.ws.security.SOAPConstants;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSEncryptionPart;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.message.WSEncryptBody;
import org.apache.ws.security.message.WSSAddUsernameToken;
import org.apache.ws.security.util.WSSecurityUtil;
import org.codehaus.xfire.security.BaseOutSecurityProcessor;
import org.codehaus.xfire.security.OutSecurityProcessor;
import org.codehaus.xfire.security.OutSecurityProcessorBuilder;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSS4JOutSecurityProcessor extends BaseOutSecurityProcessor
		implements OutSecurityProcessor {
	private OutSecurityProcessorBuilder builder = new WSS4JOutProcessorBuilder();

	private Crypto crypto;

	private String alias;

	private boolean initialized = false;

	private void checkInitialized() {
		if (!initialized) {
			builder.build(this);
			initialized = true;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.codehaus.xfire.security.OutSecurityProcessor#process(org.w3c.dom.Document)
	 */
	public Document process(Document document) {

		checkInitialized();
		SOAPConstants soapConstants = WSSecurityUtil.getSOAPConstants(document
				.getDocumentElement());
		WSEncryptBody encryptor = new WSEncryptBody();

		Vector parts = new Vector();
		WSEncryptionPart part = new WSEncryptionPart("Body", soapConstants
				.getEnvelopeURI(), "Content");

		parts.add(part);
		encryptor.setParts(parts); // this is optional since the body is
		// encrypted by default
		encryptor.setUserInfo(alias);
		try {
			document = encryptor.build(document, crypto);
		} catch (WSSecurityException e) {
			throw new RuntimeException("Can't encrypt document", e);
		}

        document = addUserToken(document);
		return document;
	}

	/**
	 * @param doc
	 * @return
	 */
	private Document addUserToken(Document doc) {
		WSSAddUsernameToken builder = new WSSAddUsernameToken();
		builder.setPasswordType(WSConstants.PASSWORD_TEXT); 
        if( getUsername()!= null ){
            Document signedDoc = builder.build(doc, getUsername(), getUserPassword());
            return signedDoc;    
        }
		return doc;

	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Crypto getCrypto() {
		return crypto;
	}

	public void setCrypto(Crypto crypto) {
		this.crypto = crypto;
	}

}
