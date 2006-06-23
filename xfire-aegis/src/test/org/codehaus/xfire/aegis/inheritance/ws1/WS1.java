package org.codehaus.xfire.aegis.inheritance.ws1;

/**
 * <br/>
 * 
 * @author xfournet
 */
public interface WS1
{
    public BeanA getBeanA();

    public BeanB getBeanB();

    public BeanA getBean(String id);

    public BeanA[] listBeans();

    public RootBean getRootBean(String id);

    public RootBean[] listRootBeans();

    public ResultBean getResultBean();

    public void throwException(boolean extendedOne)
        throws WS1Exception;
}
