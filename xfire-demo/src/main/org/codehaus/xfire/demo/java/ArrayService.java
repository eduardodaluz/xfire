package org.codehaus.xfire.demo.java;

/**
 * An array service for testing.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class ArrayService
{
    public SimpleBean[] getBeanArray()
    {
        SimpleBean bean = new SimpleBean();
        bean.setBleh("bleh");
        bean.setHowdy("howdy");
        
        return new SimpleBean[] { bean };
    }

    public String[] getStringArray()
    {
        return new String[] { "bleh", "bleh" };
    }
    /*public SimpleBean getSimpleBean()
    {
        SimpleBean bean = new SimpleBean();
        bean.setBleh("bleh");
        bean.setHowdy("howdy");
        
        return bean;
    }*/
}
