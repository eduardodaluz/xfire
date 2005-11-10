package org.codehaus.xfire.security;

import java.security.Key;

import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public interface OutSecurityProcessor
{

    void setEncCipherName(String name);

    String getEncCipherName();

    void setSymmetricCipherName(String name);

    String getSymmetricCipherName();

    void setEncryptionKey(Key encKey);

    Key getEncryptionKey();

    void setSymmetricKey(Key symmetricKey);

    Key getSymmetricKey();

    void setUsername(String user);

    String getUsername();

    void setUserPassword(String passord);

    String getUserPassword();

    void setBuilder(OutSecurityProcessorBuilder builder);

    public Document process(Document document);

}