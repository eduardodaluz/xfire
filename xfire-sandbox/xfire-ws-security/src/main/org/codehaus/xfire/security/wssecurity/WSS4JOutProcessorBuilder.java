package org.codehaus.xfire.security.wssecurity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.security.OutSecurityProcessor;
import org.codehaus.xfire.security.OutSecurityProcessorBuilder;
import org.codehaus.xfire.security.SecurityActions;
import org.codehaus.xfire.security.exceptions.ConfigValidationException;
import org.codehaus.xfire.security.impl.SecurityFileConfigurer;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSS4JOutProcessorBuilder
    extends SecurityFileConfigurer
    implements OutSecurityProcessorBuilder
{

    private static final Log LOG = LogFactory.getLog(WSS4JOutProcessorBuilder.class);
    
    public static final String CFG_FILE = "META-INF/xfire/outsecurity.properties";
    

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.xfire.security.OutSecurityProcessorBuilder#build(org.codehaus.xfire.security.OutSecurityProcessor)
     */
    public void build(OutSecurityProcessor processor)
    {
        WSS4JOutSecurityProcessor wss4jProcessor = (WSS4JOutSecurityProcessor) processor;

        Properties props = loadConfigFile(getConfigFile());
        validateProperties(props);

        String actionsStr = props.getProperty(PROP_ACTIONS);
        String actions[] = actionsToArray(actionsStr);
        wss4jProcessor.setActions(actions);

        String alias = props.getProperty(PROP_PUBLIC_ALIAS);
        String userName = props.getProperty(PROP_USER_NAME);
        String userPassword = props.getProperty(PROP_USER_PASSWORD);
        String usePlainPass = props.getProperty(PROP_USER_PASSWORD_USE_PLAIN);

        wss4jProcessor.setAlias(alias);
        for (int i = 0; i < actions.length; i++)
        {
            String action = actions[i];
            if (SecurityActions.AC_ENCRYPT.equals(action)
                    || SecurityActions.AC_SIGNATURE.equals(action))
            {
                wss4jProcessor.setCrypto(createCrypto(props));
                break;
            }
        }

        if (userName != null)
        {
            wss4jProcessor.setUsername(userName);
            wss4jProcessor.setUserPassword(userPassword);
            if (usePlainPass != null && !"false".equals(usePlainPass.toLowerCase()))
            {
                wss4jProcessor.setUsePlainUserPassword(true);
            }
        }

        String ttlStr = props.getProperty(PROP_TIME_TO_LIVE);
        if (ttlStr != null)
        {
            int ttl = Integer.parseInt(ttlStr);
            wss4jProcessor.setTTL(ttl);
        }

        wss4jProcessor.setPrivateAlias(props.getProperty(PROP_PRIVATE_ALIAS));
        wss4jProcessor.setPrivatePassword(props.getProperty(PROP_PRIVATE_PASSWORD));
    }

   

    /**
     * @param props
     */
    protected void validateProperties(Properties props)
    {

        if (props.getProperty(PROP_ACTIONS) == null)
        {
            throw new ConfigValidationException("", "Missing '" + PROP_ACTIONS + "' property ");
        }
        // Check if all specified actions are known
        String[] actions = actionsToArray(props.getProperty(PROP_ACTIONS));
        for (int i = 0; i < actions.length; i++)
        {
            String action = actions[i];
            if (actionsMap.get(action) == null)
            {
                LOG.error("Unknown action found : " + action);
                throw new ConfigValidationException(action, "Unknown action.");
            }
            if (SecurityActions.AC_ENCRYPT.equals(action))
            {
                validateEncryptCfg(props);
                continue;
            }

            if (SecurityActions.AC_SIGNATURE.equals(action))
            {
                validateSignatureCfg(props);
                continue;
            }

            if (SecurityActions.AC_TIMESTAMP.equals(action))
            {
                validateTimestampCfg(props);
                continue;
            }

            if (SecurityActions.AC_USERTOKEN.equals(action))
            {
                validateUsertokenCfg(props);
                continue;
            }

        }

    }

    /**
     * @param props
     */
    private void validateUsertokenCfg(Properties props)
    {
        checkRequiredProperty(SecurityActions.AC_USERTOKEN, new String[] { PROP_USER_PASSWORD,
                PROP_USER_NAME, }, props);
        String usePlain = (String) props.get(PROP_USER_PASSWORD_USE_PLAIN);
        if (usePlain != null && "true".equals(usePlain.toLowerCase()))
        {
            LOG.warn("Property '" + PROP_USER_PASSWORD_USE_PLAIN + "' value is not a boolen value");
        }

    }

    /**
     * @param props
     */
    private void validateTimestampCfg(Properties props)
    {

        checkRequiredProperty(SecurityActions.AC_TIMESTAMP,
                              new String[] { PROP_TIME_TO_LIVE, },
                              props);
        try
        {
            Long.parseLong((String) props.get(PROP_TIME_TO_LIVE));
        }
        catch (Throwable ex)
        {
            throw new ConfigValidationException(SecurityActions.AC_TIMESTAMP, "Property : '"
                    + PROP_TIME_TO_LIVE + "' is not a digit");
        }

    }

    /**
     * @param props
     */
    private void validateSignatureCfg(Properties props)
    {
        validateKeystore(props);
        checkRequiredProperty(SecurityActions.AC_SIGNATURE, new String[] { PROP_PRIVATE_ALIAS,
                PROP_PRIVATE_PASSWORD, }, props);
    }

    /**
     * @param props
     */
    private void validateEncryptCfg(Properties props)
    {
        validateKeystore(props);
        checkRequiredProperty(SecurityActions.AC_ENCRYPT, new String[] { PROP_KEYSTORE_FILE,
                PROP_KEYSTORE_PASS, PROP_KEYSTORE_TYPE }, props);
        // PROP_PRIVATE_ALIAS, PROP_KEY_ALIAS,
        // PROP_SYM_ALG, PROP_CERT_FILE, ,
        // 
    }

    protected String getDefaultConfigFile()
    {
        return CFG_FILE;
    }

}
