package org.codehaus.xfire.config;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class ConfigurationException
    extends Exception
{
    /**
     * @param message
     */
    public ConfigurationException(String message)
    {
        super(message);
    }

    public ConfigurationException(String message, Throwable t)
    {
        super(message, t);
    }
}
