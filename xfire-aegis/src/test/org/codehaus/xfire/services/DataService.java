package org.codehaus.xfire.services;

public class DataService
{
    public DataBean getData()
    {
        return new DataBean();
    }
    
    public DataBean echoData(DataBean bean)
    {
        return bean;
    }
}
