package org.codehaus.xfire.spring.config;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 * @org.xbean.XBean element="schema" contentProperty="location" 
 * 
 */
public class SchemaBean
    implements FactoryBean
{
    private String location;

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject()
        throws Exception
    {
        return location;
    }

    public Class getObjectType()
    {
        return String.class;

    }

    public boolean isSingleton()
    {
        return false;
    }
}
