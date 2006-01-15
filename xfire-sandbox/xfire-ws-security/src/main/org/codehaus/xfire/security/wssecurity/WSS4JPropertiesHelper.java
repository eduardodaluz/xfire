package org.codehaus.xfire.security.wssecurity;

import java.util.Properties;

import org.codehaus.xfire.security.impl.SecurityFileConfigurer;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class WSS4JPropertiesHelper
{

    /**
     * @param props
     * @return
     */
    public static Properties buildWSS4JProps(Properties props)
    {
        Properties wss4jProps = new Properties();

        wss4jProps.put("org.apache.ws.security.crypto.provider",
                       "org.apache.ws.security.components.crypto.Merlin");
        wss4jProps.put("org.apache.ws.security.crypto.merlin.keystore.type", "jks");
        String keyStorePass = props.getProperty(SecurityFileConfigurer.PROP_KEYSTORE_PASS);
        wss4jProps.put("org.apache.ws.security.crypto.merlin.keystore.password", keyStorePass);
        String keyAlias = props.getProperty(SecurityFileConfigurer.PROP_KEY_ALIAS);
        wss4jProps.put("org.apache.ws.security.crypto.merlin.keystore.alias", keyAlias);
        String keyPass = props.getProperty(SecurityFileConfigurer.PROP_PRIVATE_PASSWORD);
        if (keyPass != null)
        {
            wss4jProps.put("org.apache.ws.security.crypto.merlin.alias.password", keyPass);
        }
        String keyStore = props.getProperty(SecurityFileConfigurer.PROP_KEYSTORE_FILE);
        wss4jProps.put("org.apache.ws.security.crypto.merlin.file", keyStore);

        return wss4jProps;
    }
}
