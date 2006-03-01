package org.codehaus.xfire.security.wssecurity;

import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.xfire.security.handlers.InSecurityHandler;
import org.codehaus.xfire.security.impl.PropertiesLoader;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSS4JInSecurityHandler
    extends InSecurityHandler
{
    private static final String CFG_FILE = "META-INF/xfire/insecurity.properties";

    private String configFile=CFG_FILE;

    public WSS4JInSecurityHandler()
    {
        super();
        setProcessor(new WSS4JInSecurityProcessor());
       // setConfigFile(configFile);

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

    public QName[] getUnderstoodHeaders()
    {
     
        return new QName[]{new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd","Security")};
    }
}
