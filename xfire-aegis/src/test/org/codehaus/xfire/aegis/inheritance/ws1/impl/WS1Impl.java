package org.codehaus.xfire.aegis.inheritance.ws1.impl;

import java.util.Map;

import org.codehaus.xfire.aegis.inheritance.ws1.BeanA;
import org.codehaus.xfire.aegis.inheritance.ws1.BeanB;
import org.codehaus.xfire.aegis.inheritance.ws1.BeanC;
import org.codehaus.xfire.aegis.inheritance.ws1.ResultBean;
import org.codehaus.xfire.aegis.inheritance.ws1.RootBean;
import org.codehaus.xfire.aegis.inheritance.ws1.WS1;
import org.codehaus.xfire.aegis.inheritance.ws1.WS1Exception;
import org.codehaus.xfire.aegis.inheritance.ws1.WS1ExtendedException;
import org.codehaus.xfire.aegis.type.basic.SimpleBean;

/**
 * <br/>
 * 
 * @author xfournet
 */
public class WS1Impl
    implements WS1
{
    public BeanA getBeanA()
    {
        BeanA a = new BeanA();
        a.setPropA("valueA");
        return a;
    }

    public BeanB getBeanB()
    {
        BeanB b = new BeanB();
        b.setPropA("valueA");
        b.setPropB("valueB");
        return b;
    }

    // not exported to interface to "hide" BeanC from interface introspection
    public BeanC getBeanC()
    {
        BeanC c = new BeanC();
        c.setPropA("valueA");
        c.setPropB("valueB");
        c.setPropC("valueC");
        return c;
    }

    public BeanA getBean(String id)
    {
        if ("b".equalsIgnoreCase(id))
        {
            return getBeanB();
        }
        else if ("c".equalsIgnoreCase(id))
        {
            return getBeanC();
        }
        else if ("a".equalsIgnoreCase(id))
        {
            return getBeanA();
        }
        else
        {
            return null;
        }
    }

    public BeanA[] listBeans()
    {
        BeanA[] result = new BeanA[4];

        result[0] = getBean("b");
        result[1] = null;
        result[2] = getBean("a");
        result[3] = getBean("c");

        return result;
    }

    public RootBean getRootBean(String id)
    {
        RootBean rootBean = new RootBean();
        rootBean.setId(id);
        rootBean.setChild(getBean(id));

        return rootBean;
    }

    public RootBean[] listRootBeans()
    {
        RootBean[] result = new RootBean[4];

        result[0] = getRootBean("b");
        result[1] = null;
        result[2] = getRootBean("a");
        result[3] = getRootBean("c");

        return result;
    }

    public ResultBean getResultBean()
    {
        ResultBean resultBean = new ResultBean();
        resultBean.setResult1(listBeans());
        resultBean.setResult2(listRootBeans());

        return resultBean;
    }

    public Map echoMap(Map beans)
    {
        return beans;
    }

    public void throwException(boolean extendedOne)
        throws WS1Exception
    {
        if (extendedOne)
        {
            WS1Exception ex = new WS1ExtendedException("WS1 extended exception", 20, 30);
            ex.setSimpleBean(new SimpleBean());
            throw ex;
        }
        else
        {
            throw new WS1Exception("WS1 base exception", 10);
        }
    }
}
