package org.codehaus.xfire.security;


/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public interface OutSecurityProcessor extends SecurityProcessor
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

    

}
