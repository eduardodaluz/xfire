package org.codehaus.xfire.aegis.inheritance.ws1;

import java.util.Arrays;

/**
 * <br/>
 * 
 * @author xfournet
 */
public class ResultBean
{
    private BeanA[] m_result1;

    private RootBean[] m_result2;

    public BeanA[] getResult1()
    {
        return m_result1;
    }

    public void setResult1(BeanA[] result1)
    {
        m_result1 = result1;
    }

    public RootBean[] getResult2()
    {
        return m_result2;
    }

    public void setResult2(RootBean[] result2)
    {
        m_result2 = result2;
    }

    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        final ResultBean that = (ResultBean) o;

        if (!Arrays.equals(m_result1, that.m_result1))
        {
            return false;
        }
        if (!Arrays.equals(m_result2, that.m_result2))
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return 0;
    }
}
