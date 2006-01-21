package org.codehaus.xfire.security;

import org.codehaus.xfire.security.exceptions.WSSecurityException;
import org.w3c.dom.Document;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public interface InSecurityProcessor
{

    InSecurityResult process(Document document)
        throws WSSecurityException;

    WSPasswordCallback getCallback();

    void setCallback(WSPasswordCallback callback);
}
