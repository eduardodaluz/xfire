package org.codehaus.xfire.security.wssecurity;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.codehaus.xfire.security.OutSecurityProcessor;
import org.codehaus.xfire.security.OutSecurityProcessorBuilder;
import org.codehaus.xfire.security.impl.SecurityFileConfigurer;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSS4JOutProcessorBuilder
    extends SecurityFileConfigurer
    implements OutSecurityProcessorBuilder
{

    private static final String CFG_FILE = "META-INF/xfire/outsecurity.properties";

    private static final Log LOG = LogFactory.getLog(WSS4JOutProcessorBuilder.class);

    public void build(OutSecurityProcessor processor)
    {
        WSS4JOutSecurityProcessor wss4jProcessor = (WSS4JOutSecurityProcessor) processor;

        Properties props = loadConfigFile(CFG_FILE);

        Properties wss4j = WSS4JPropertiesHelper.buildWSS4JProps(props);
        String alias = props.getProperty(PROP_KEY_ALIAS);
        String userName = props.getProperty(PROP_USER_NAME);
        String userPassword = props.getProperty(PROP_USER_PASSWORD);
        Crypto crypto = CryptoFactory
                .getInstance("org.apache.ws.security.components.crypto.Merlin", wss4j);

        wss4jProcessor.setAlias(alias);
        wss4jProcessor.setCrypto(crypto);
        if (userName != null)
        {
            wss4jProcessor.setUsername(userName);
            wss4jProcessor.setUserPassword(userPassword);
        }

    }

}
