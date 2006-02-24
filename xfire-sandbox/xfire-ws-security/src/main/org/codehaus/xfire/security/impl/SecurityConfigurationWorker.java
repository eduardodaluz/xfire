package org.codehaus.xfire.security.impl;

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
public abstract class SecurityConfigurationWorker
    implements SecurityProperties
{

    private static final Log LOG = LogFactory.getLog(SecurityConfigurationWorker.class);

    protected static Map actionsMap = new HashMap();

    protected Map configuration = new HashMap();

    static
    {
        for (int a = 0; a < SecurityActions.ALL_ACTIONS.length; a++)
        {
            actionsMap.put(SecurityActions.ALL_ACTIONS[a], Boolean.TRUE);
        }
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

    /**
     * @param props
     * @return
     */
    protected Crypto createCrypto(Map props)
    {
        Properties wss4j = WSS4JPropertiesHelper.buildWSS4JProps(props);
        Crypto crypto = CryptoFactory
                .getInstance("org.apache.ws.security.components.crypto.Merlin", wss4j);
        return crypto;
    }

    /**
     * @param props
     */
    protected void validateKeystore(Map props)
    {
        checkRequiredProperty("", new String[] { PROP_KEYSTORE_FILE, PROP_KEYSTORE_PASS, }, props);
    }

    /**
     * @param action
     * @param properties
     * @param props
     */
    protected void checkRequiredProperty(String action, String[] properties, Map props)
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

    public Map getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration(Map properties)
    {
        this.configuration = properties;
    }

}
