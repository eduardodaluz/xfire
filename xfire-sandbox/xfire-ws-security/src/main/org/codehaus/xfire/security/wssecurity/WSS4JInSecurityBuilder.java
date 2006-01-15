package org.codehaus.xfire.security.wssecurity;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.codehaus.xfire.security.InSecurityProcessor;
import org.codehaus.xfire.security.InSecurityProcessorBuilder;
import org.codehaus.xfire.security.impl.SecurityFileConfigurer;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class WSS4JInSecurityBuilder
    extends SecurityFileConfigurer
    implements InSecurityProcessorBuilder
{

    private static final String CFG_FILE = "META-INF/xfire/insecurity.properties";

    public void build(InSecurityProcessor processor)
    {
        if (!(processor instanceof WSS4JInSecurityProcessor))
        {
            throw new RuntimeException(
                    "Processor is not an instance of WSS4JInSecurityProcessor.class");
        }
        WSS4JInSecurityProcessor wss4jProcessor = (WSS4JInSecurityProcessor) processor;

        Properties props = loadConfigFile(CFG_FILE);

        Properties wss4j = WSS4JPropertiesHelper.buildWSS4JProps(props);
        String alias = props.getProperty(PROP_KEY_ALIAS);
        String password = props.getProperty(PROP_PRIVATE_PASSWORD);
        Map pass = new HashMap();
        pass.put(alias, password);

        Crypto crypto = CryptoFactory
                .getInstance("org.apache.ws.security.components.crypto.Merlin", wss4j);

        wss4jProcessor.setCrypto(crypto);
        wss4jProcessor.setPasswords(pass);
    }

}
