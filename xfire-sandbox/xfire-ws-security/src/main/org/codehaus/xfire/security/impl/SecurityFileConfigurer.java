package org.codehaus.xfire.security.impl;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.codehaus.xfire.security.SecurityActions;
import org.codehaus.xfire.security.exceptions.ConfigValidationException;
import org.codehaus.xfire.security.wssecurity.WSS4JPropertiesHelper;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 * TODO : add validation if property file contains only valid values
 */
public abstract class SecurityFileConfigurer
{

    private static final Log LOG = LogFactory.getLog(SecurityFileConfigurer.class);

    public static final String PROP_KEYSTORE_TYPE = "xfire.security.keystore.type";

    public static final String PROP_KEYSTORE_PASS = "xfire.security.keystore.password";

    public static final String PROP_KEYSTORE_FILE = "xfire.security.keystore.file";

    public static final String PROP_PUBLIC_ALIAS = "xfire.security.public.alias";

    // public static final String PROP_KEY_PASS = "xfire.security.key.password";

    public static final String PROP_CERT_FILE = "xfire.security.cert.file";

    public static final String PROP_ENC_ALG = "xfire.security.encrypt.key.algoritm";

    public static final String PROP_SYM_ALG = "xfire.security.symmetric.key.algoritm";

    public static final String PROP_USER_NAME = "xfire.security.user.name";

    public static final String PROP_USER_PASSWORD = "xfire.security.user.password";

    public static final String PROP_USER_PASSWORD_USE_PLAIN = "xfire.security.user.password.use.plain";

    public static final String PROP_TIME_TO_LIVE = "xfire.security.time.to.live";

    public static final String PROP_ACTIONS = "xfire.security.actions";

    public static final String PROP_PRIVATE_ALIAS = "xfire.security.private.alias";

    public static final String PROP_PRIVATE_PASSWORD = "xfire.security.private.password";
    
    public static final String PROP_PASSWORD_CALLBACK = "xfire.security.password.callback";

    public static final String[] ALL_PROPS = { PROP_USER_PASSWORD_USE_PLAIN, PROP_USER_PASSWORD,
            PROP_USER_NAME, PROP_SYM_ALG, PROP_CERT_FILE, PROP_PUBLIC_ALIAS, PROP_KEYSTORE_FILE,
            PROP_KEYSTORE_PASS, PROP_KEYSTORE_TYPE, PROP_TIME_TO_LIVE, PROP_ACTIONS,
            PROP_PRIVATE_ALIAS, PROP_PRIVATE_PASSWORD, PROP_PASSWORD_CALLBACK,};

    
    protected static Map actionsMap = new HashMap();

    static
    {
        for (int a = 0; a < SecurityActions.ALL_ACTIONS.length; a++)
        {
            actionsMap.put(SecurityActions.ALL_ACTIONS[a], Boolean.TRUE);
        }
    }

    
    
    private String configFile;

    
    
    
    protected abstract String getDefaultConfigFile();

    /**
     * @param path
     * @return
     */
    protected Properties loadConfigFile(String path)
    {

        Properties props = new Properties();
        InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(path);
        if (inStream == null)
        {
            throw new RuntimeException("Security config file not found :" + path);
        }

        try
        {
            props.load(inStream);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if (inStream != null)
            {
                try
                {
                    inStream.close();
                }
                catch (IOException e)
                {

                }
            }
        }

        return props;
    }

    /**
     * @param inStream
     * @param keyStoreType
     * @param keyStorePassword
     */
    protected KeyStore loadKeyStore(InputStream inStream,
                                    String keyStoreType,
                                    String keyStorePassword)
    {
        try
        {
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(inStream, keyStorePassword.toCharArray());
            return keyStore;
        }
        catch (Exception e)
        {
            LOG.error(e);
            throw new RuntimeException("Unable to load keyStore: ", e);
        }

    }

    /**
     * @param keystore
     * @param alias
     * @param pass
     * @return
     */
    protected PrivateKey getPrivateKey(KeyStore keystore, String alias, String pass)
        throws Exception
    {
        return (PrivateKey) keystore.getKey(alias, pass.toCharArray());
    }

    /**
     * @param keystore
     * @param alias
     * @return
     * @throws Exception
     */
    protected Certificate getCertificate(KeyStore keystore, String alias)
        throws Exception
    {
        return keystore.getCertificate(alias);
    }

    public void setConfigFile(String configFile)
    {
        this.configFile = configFile;
    }

    /**
     * @return
     */
    public String getConfigFile()
    {
        return (configFile == null ? getDefaultConfigFile() : configFile);
    }

    /**
     * @param props
     * @return
     */
    protected Crypto createCrypto(Properties props)
    {
        Properties wss4j = WSS4JPropertiesHelper.buildWSS4JProps(props);
        Crypto crypto = CryptoFactory
                .getInstance("org.apache.ws.security.components.crypto.Merlin", wss4j);
        return crypto;
    }
    
    /**
     * @param props
     */
    protected void validateKeystore(Properties props){
        //String certFile = (String) props.get(PROP_CERT_FILE);
        String keystore = props.getProperty(PROP_KEYSTORE_FILE);
        String keystorePass = props.getProperty(PROP_KEYSTORE_PASS);
        checkRequiredProperty("",new String[]{PROP_KEYSTORE_FILE,PROP_KEYSTORE_PASS,},props);
    }
    
    /**
     * @param action
     * @param properties
     * @param props
     */
    protected void checkRequiredProperty(String action, String[] properties, Properties props)
    {
        // TODO : cumulate problems, so user can see all of them at once
        for (int i = 0; i < properties.length; i++)
        {
            if (props.get(properties[i]) == null)
            {
                throw new ConfigValidationException(action, "Missing required property : '"
                        + props.get(properties[i]) + "'");
            }
        }
    }
    
    /**
     * @param actionsStr
     * @return
     */
    protected String[] actionsToArray(String actionsStr)
    {
        StringTokenizer tokenizer = new StringTokenizer(actionsStr);
        Collection actions = new ArrayList();
        while (tokenizer.hasMoreTokens())
        {
            actions.add(((String) tokenizer.nextToken()).toLowerCase());
        }
        return (String[]) actions.toArray(new String[actions.size()]);

    }
}
