package org.codehaus.xfire.security;

import java.security.Key;

import org.codehaus.xfire.security.exceptions.WSSecurityException;
import org.w3c.dom.Document;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public interface InSecurityProcessor {

	void setDecryptionKey(Key decryptionKey);
	
	InSecurityResult process(Document document) throws WSSecurityException;
	
	void setBuilder(InSecurityProcessorBuilder builder);
	

}
