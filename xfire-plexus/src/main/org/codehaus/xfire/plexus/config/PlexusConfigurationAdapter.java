package org.codehaus.xfire.plexus.config;

import java.util.ArrayList;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.xfire.config.AbstractConfiguration;
import org.codehaus.xfire.config.Configuration;
import org.codehaus.xfire.config.ConfigurationException;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PlexusConfigurationAdapter
    extends AbstractConfiguration
    implements Configuration
{
    private PlexusConfiguration config;
    private Configuration[] children;
    
    public PlexusConfigurationAdapter(PlexusConfiguration config)
    {
        this.config = config;
    }

    protected String getPrefix()
        throws ConfigurationException
    {
        return "";
    }

    public String getName()
    {
        return config.getName();
    }

    public String getLocation()
    {
        return "unknown location";
    }

    public String getNamespace()
        throws ConfigurationException
    {
        return "";
    }

    public Configuration[] getChildren()
    {
        if ( children == null )
        {
            ArrayList childList = new ArrayList(config.getChildCount());
            for ( int i = 0; i < config.getChildCount(); i++ )
            {
                childList.add(new PlexusConfigurationAdapter(config.getChild(i)));
            }
            children = (Configuration[]) childList.toArray(new Configuration[config.getChildCount()]);
        }
        return children;
    }

    /**
     */
    public Configuration[] getChildren(String name)
    {
        Configuration[] children = getChildren();
        
        ArrayList namedChildren = new ArrayList();
        for ( int i = 0; i < children.length; i++ )
        {
            if (children[i].getName().equals(name))
                namedChildren.add(children[i]);
        }

        return (Configuration[]) namedChildren.toArray(new Configuration[namedChildren.size()]);
    }

    /**
     */
    public String[] getAttributeNames()
    {
        return config.getAttributeNames();
    }

    /**
     */
    public String getAttribute(String paramName)
        throws ConfigurationException
    {
        try
        {
            return config.getAttribute(paramName);
        }
        catch (PlexusConfigurationException e)
        {
            throw new ConfigurationException(e.getMessage(), e.getCause());
        }
    }

    /**
     */
    public String getValue()
        throws ConfigurationException
    {
        try
        {
            return config.getValue();
        }
        catch (PlexusConfigurationException e)
        {
            throw new ConfigurationException(e.getMessage(), e.getCause());
        }
    }

}
