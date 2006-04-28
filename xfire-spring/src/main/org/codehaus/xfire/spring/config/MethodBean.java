package org.codehaus.xfire.spring.config;

import java.util.List;
import java.util.Map;

/**
 * @org.apache.xbean.XBean element="method"
 */
public class MethodBean
{
    private String operationName;
    private String name;
    private List parameters;
    private Map properties;
    private boolean exclude;
    private String mep;
    private String soapAction;
    private ParameterBean returnType;
    
    public String getSoapAction()
    {
        return soapAction;
    }
    public void setSoapAction(String soapAction)
    {
        this.soapAction = soapAction;
    }
    public String getMep()
    {
        return mep;
    }
    public void setMep(String mep)
    {
        this.mep = mep;
    }
    public boolean isExclude()
    {
        return exclude;
    }
    public void setExclude(boolean exclude)
    {
        this.exclude = exclude;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getOperationName()
    {
        return operationName;
    }
    public void setOperationName(String operationName)
    {
        this.operationName = operationName;
    }
    public Map getProperties()
    {
        return properties;
    }
    public void setProperties(Map properties)
    {
        this.properties = properties;
    }

    /**
     * @org.apache.xbean.Flat
     * @return
     */
    public ParameterBean getReturnType()
    {
        return returnType;
    }
    
    public void setReturnType(ParameterBean returnType)
    {
        this.returnType = returnType;
    }
    /**
     * @org.apache.xbean.FlatCollection childElement="parameter"
     * @return
     */
    public List getParameters()
    {
        return parameters;
    }
    public void setParameters(List parameters)
    {
        this.parameters = parameters;
    }
}
