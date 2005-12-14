package org.codehaus.xfire.spring.config;

import javax.xml.namespace.QName;

/**
 * @org.xbean.XBean element="endpoint"
 */
public class EndpointBean
{
    private QName name;
    private String url;
    
    public String getUrl()
    {
        return url;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }
    public QName getName()
    {
        return name;
    }
    public void setName(QName name)
    {
        this.name = name;
    }
}
