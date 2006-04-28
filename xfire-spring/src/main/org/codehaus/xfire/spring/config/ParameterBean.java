package org.codehaus.xfire.spring.config;

import javax.xml.namespace.QName;

/**
 * @org.apache.xbean.XBean element="parameter"
 */
public class ParameterBean
{
    private String clazz;
    private int index = Integer.MAX_VALUE;
    private QName name;
    private Boolean header;
    private String mode;
    
    /**
     * Specifies whether this parameter is "in", "inout", or "out".
     * @return
     */
    public String getMode()
    {
        return mode;
    }
    public void setMode(String mode)
    {
        this.mode = mode;
    }
    public QName getName()
    {
        return name;
    }
    public void setName(QName name)
    {
        this.name = name;
    }
    /**
     * @org.apache.xbean.Property alias="class"
     */
    public String getClazz()
    {
        return clazz;
    }
    public void setClazz(String clazz)
    {
        this.clazz = clazz;
    }
    public Boolean getHeader()
    {
        return header;
    }
    public void setHeader(Boolean header)
    {
        this.header = header;
    }
    public int getIndex()
    {
        return index;
    }
    public void setIndex(int index)
    {
        this.index = index;
    }    
}
