package org.codehaus.xfire.aegis.inheritance.ws1;

/**
 * <br/>
 * 
 * @author xfournet
 */
public class BeanA
    implements java.io.Serializable
{
    private String m_propA;

    public String getPropA()
    {
        return m_propA;
    }

    public void setPropA(String propA)
    {
        m_propA = propA;
    }

    public String toString()
    {
        return "[" + getClass().getName() + "] propA=" + m_propA;
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

        final BeanA beanA = (BeanA) o;

        if ((m_propA != null) ? (!m_propA.equals(beanA.m_propA)) : (beanA.m_propA != null))
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return (m_propA != null ? m_propA.hashCode() : 0);
    }
}
