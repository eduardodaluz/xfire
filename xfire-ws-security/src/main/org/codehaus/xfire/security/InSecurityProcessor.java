package org.codehaus.xfire.security;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public interface InSecurityProcessor extends SecurityProcessor {

    WSPasswordCallback getCallback();

    void setCallback(WSPasswordCallback callback);
}
