package org.codehaus.xfire.config;

/**
 * Indicates that a particular class can be configured from a 
 * <code>Configuration</code>. All classes which support the Configurable
 * interface must also be able to be configured via their API.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface Configurable
{
    public void configure(Configuration configuration) 
        throws ConfigurationException;
}
