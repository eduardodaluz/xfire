package org.codehaus.xfire.security.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class PropertiesLoader
{
    private String configFile;

    public Map loadConfigFile(String path)
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
        // Trim unnecessary white spaces
        Iterator iter = props.keySet().iterator();
        while (iter.hasNext())
        {
            String key = (String) iter.next();
            props.put(key, ((String) props.getProperty(key).trim()));
        }
        return props;
    }

    public void setConfigFile(String configFile)
    {
        this.configFile = configFile;
    }

    public String getConfigFile()
    {
        return configFile;
    }

}
