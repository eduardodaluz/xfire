package org.codehaus.xfire.spring.config;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 * @org.xbean.XBean element="sproperty" contentProperty="value"
 * 
 */
public class ServicePropertyBean
    implements FactoryBean
{

    private String key;

    private Object value;

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        QName qname = (QName) value;
        
        this.value = qname.getLocalPart();
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject()
        throws Exception
    {
        // TODO : reimplement this 
        // This is only temporary solution to pass 2 values at once
        Map map = new HashMap();
        map.put(key, value);
        Object obj = map.entrySet().iterator().next();
        return obj;
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
