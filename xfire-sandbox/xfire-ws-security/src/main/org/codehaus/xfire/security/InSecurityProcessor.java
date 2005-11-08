package org.codehaus.xfire.security;

import java.security.Key;

import org.w3c.dom.Document;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public interface InSecurityProcessor {

	void setDecryptionKey(Key decryptionKey);
	
	InSecurityResult process(Document document);
	
	void setBuilder(InSecurityProcessorBuilder builder);
	

}
