package org.codehaus.xfire.security.wssecurity;

import java.util.Map;

import org.codehaus.xfire.security.handlers.OutSecurityHandler;
import org.codehaus.xfire.security.impl.PropertiesLoader;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSS4JOutSecurityHandler
    extends OutSecurityHandler
{
    private static final String CFG_FILE = "META-INF/xfire/outsecurity.properties";

    private String configFile;

    public WSS4JOutSecurityHandler()
    {
        super();
        setProcessor(new WSS4JOutSecurityProcessor());

    }
    
    public WSS4JOutSecurityHandler(Map props)
    {
        this();
        setConfiguration(props);
    }

    public WSS4JOutSecurityHandler(String file)
    {
        this();
        setConfigFile(file);
    }

    
    public String getConfigFile()
    {
        return configFile;
    }

    public void setConfigFile(String configFile)
    {
        this.configFile = configFile;
        if (configFile != null)
        {
            setConfiguration(new PropertiesLoader().loadConfigFile(configFile));
        }

    }

}
