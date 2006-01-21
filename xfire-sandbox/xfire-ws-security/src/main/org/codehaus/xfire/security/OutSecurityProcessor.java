package org.codehaus.xfire.security;

import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public interface OutSecurityProcessor
{

    void setUsername(String user);

    String getUsername();

    void setUserPassword(String passord);

    String getUserPassword();

    void setUsePlainUserPassword(boolean usePlain);

    boolean usePlainUserPassword();

    int getTTL();

    void setTTL(int ttl);

    void setPrivateAlias(String alias);

    String getPrivateAlias();

    void setPrivatePassword(String pass);

    String getPrivatePassword();

    public Document process(Document document);

}
