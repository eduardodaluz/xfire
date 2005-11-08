package org.codehaus.xfire.security;

import java.security.Key;

import org.w3c.dom.Document;

/**
 *<a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
abstract public class BaseOutSecurityProcessor
    implements OutSecurityProcessor
{

    private String encCipherName;

    private String symmetricCipherName;

    private Key encryptionKey;

    private Key symmetricKey;

    private String username;

    private String userPassword;

    private OutSecurityProcessorBuilder builder;

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

}
