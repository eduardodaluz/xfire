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
public class WSS4JInProcessorBuilder
    extends SecurityFileConfigurer
    implements InSecurityProcessorBuilder
{

    private static final String CFG_FILE = "META-INF/xfire/insecurity.properties";

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.xfire.security.InSecurityProcessorBuilder#build(org.codehaus.xfire.security.InSecurityProcessor)
     */
    public void build(InSecurityProcessor processor)
    {
        if (!(processor instanceof WSS4JInSecurityProcessor))
        {
            throw new RuntimeException(
                    "Processor is not an instance of WSS4JInSecurityProcessor.class");
        }
        WSS4JInSecurityProcessor wss4jProcessor = (WSS4JInSecurityProcessor) processor;

        Properties props = loadConfigFile(getConfigFile());

               
        String alias = props.getProperty(PROP_PRIVATE_ALIAS);
        String password = props.getProperty(PROP_PRIVATE_PASSWORD);
        Map pass = new HashMap();
        pass.put(alias, password);

        Crypto crypto = createCrypto(props);

        wss4jProcessor.setCrypto(crypto);
        wss4jProcessor.setPasswords(pass);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.xfire.security.impl.SecurityFileConfigurer#getDefaultConfigFile()
     */
    protected String getDefaultConfigFile()
    {
        return CFG_FILE;
    }

}
