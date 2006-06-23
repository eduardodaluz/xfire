package org.codehaus.xfire.aegis.inheritance.ws1;

/**
 * <br/>
 * 
 * @author xfournet
 */
public class RootBean
{
    private String m_id;

    private BeanA m_child;

    public String getId()
    {
        return m_id;
    }

    public void setId(String id)
    {
        m_id = id;
    }

    public BeanA getChild()
    {
        return m_child;
    }

    public void setChild(BeanA child)
    {
        m_child = child;
    }

    public String toString()
    {
        return "[" + getClass().getName() + "] id=" + m_id + "; child={" + m_child + "}";
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

        final RootBean rootBean = (RootBean) o;

        if (m_child != null ? !m_child.equals(rootBean.m_child) : rootBean.m_child != null)
        {
            return false;
        }
        if (m_id != null ? !m_id.equals(rootBean.m_id) : rootBean.m_id != null)
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        int result;
        result = (m_id != null ? m_id.hashCode() : 0);
        result = 29 * result + (m_child != null ? m_child.hashCode() : 0);
        return result;
    }
}
