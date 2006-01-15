package org.codehaus.xfire.security;

import java.security.Key;

import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class DebugOutSecurityProcesor
    implements OutSecurityProcessor
{

    private String encCipherName;

    private String symmetricCipherName;

    private Key encryptionKey;

    private Key symmetricKey;

    private String username;

    private String userPassword;

    private boolean usePlainUserPass;

    private OutSecurityProcessorBuilder builder;

    private int ttl;

    private String privateAlias;

    private String privatePassword;

    public void setEncCipherName(String name)
    {
        encCipherName = name;

    }

    public void setSymmetricCipherName(String name)
    {
        symmetricCipherName = name;

    }

    public void setEncryptionKey(Key encKey)
    {
        encryptionKey = encKey;

    }

    public void setSymmetricKey(Key symmetricKey)
    {
        this.symmetricKey = symmetricKey;

    }

    public void setUsername(String user)
    {
        this.username = user;

    }

    public void setUserPassword(String password)
    {
        this.userPassword = password;

    }

    public void setBuilder(OutSecurityProcessorBuilder builder)
    {
        this.builder = builder;

    }

    public Document process(Document document)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getEncCipherName()
    {

        return encCipherName;
    }

    public String getSymmetricCipherName()
    {
        return symmetricCipherName;
    }

    public Key getEncryptionKey()
    {

        return encryptionKey;
    }

    public Key getSymmetricKey()
    {

        return symmetricKey;
    }

    public String getUsername()
    {

        return username;
    }

    public String getUserPassword()
    {

        return userPassword;
    }

    public void setUsePlainUserPassword(boolean usePlain)
    {
        usePlainUserPass = usePlain;

    }

    public boolean usePlainUserPassword()
    {

        return usePlainUserPass;
    }

    public int getTTL()
    {
        return ttl;
    }

    public void setTTL(int ttl)
    {
        this.ttl = ttl;

    }

    public void setPrivateAlias(String alias)
    {
        this.privateAlias = alias;
        
    }

    public String getPrivateAlias()
    {

        return privateAlias;
    }

    public void setPrivatePassword(String pass)
    {
        this.privatePassword = pass;
        
    }

    public String getPrivatePassword()
    {

        return privatePassword;
    }

}
