package org.codehaus.xfire.security.wssecurity;

import java.util.Map;

import org.codehaus.xfire.security.handlers.InSecurityHandler;
import org.codehaus.xfire.security.impl.PropertiesLoader;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSS4JInSecurityHandler
    extends InSecurityHandler
{

    private String configFile;

    public WSS4JInSecurityHandler()
    {
        super();
        setProcessor(new WSS4JInSecurityProcessor());

    }

    public WSS4JInSecurityHandler(Map props)
    {
        this();
        setConfiguration(props);
    }

    public WSS4JInSecurityHandler(String file)
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
