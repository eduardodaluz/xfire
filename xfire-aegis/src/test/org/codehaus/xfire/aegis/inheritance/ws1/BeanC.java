package org.codehaus.xfire.aegis.inheritance.ws1;

/**
 * <br/>
 * 
 * @author xfournet
 */
public class BeanC
    extends BeanB
{
    private String m_propC;

    public String getPropC()
    {
        return m_propC;
    }

    public void setPropC(String propC)
    {
        m_propC = propC;
    }

    public String toString()
    {
        return super.toString() + " ; propC=" + m_propC;
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
        if (!super.equals(o))
        {
            return false;
        }

        final BeanC beanC = (BeanC) o;

        if ((m_propC != null) ? (!m_propC.equals(beanC.m_propC)) : (beanC.m_propC != null))
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        int result = super.hashCode();
        result = 29 * result + (m_propC != null ? m_propC.hashCode() : 0);
        return result;
    }
}
