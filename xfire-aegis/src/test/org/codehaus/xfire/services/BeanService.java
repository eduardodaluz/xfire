package org.codehaus.xfire.services;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class BeanService
{
    public SimpleBean getSimpleBean()
    {
        SimpleBean bean = new SimpleBean();
        bean.setBleh("bleh");
        bean.setHowdy("howdy");
        
        return bean;
    }
    
    public String getSubmitBean( SimpleBean bean, String bleh )
    {
        return bean.getBleh();
    }

}
