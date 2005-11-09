package org.codehaus.xfire.spring.config;

import java.util.Map;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 * @org.xbean.XBean element="property" contentProperty="value" 
 * 
 */
public class ServicePropertyBean
    implements FactoryBean
{

    private String key;

    private String value;

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject()
        throws Exception
    {
        return new Object[] { key, value };
    }

    public Class getObjectType()
    {
        return Map.Entry.class;

    }

    public boolean isSingleton()
    {
        return false;
    }

}
