package org.codehaus.xfire.aegis.inheritance.ws2.common.pack2;

import org.codehaus.xfire.aegis.inheritance.ws2.common.pack1.ContentBean1;

/**
 * <br/>
 * 
 * @author xfournet
 */
public class ContentBean2
    extends ContentBean1
{
    private String m_content2;

    public ContentBean2()
    {
    }

    public ContentBean2(String data1, String content2)
    {
        super(data1);
        m_content2 = content2;
    }

    public String getContent2()
    {
        return m_content2;
    }

    public void setContent2(String content2)
    {
        m_content2 = content2;
    }

    public String toString()
    {
        return super.toString() + "; content2=" + m_content2;
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

        final ContentBean2 that = (ContentBean2) o;

        if (m_content2 != null ? !m_content2.equals(that.m_content2) : that.m_content2 != null)
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        int result = super.hashCode();
        result = 29 * result + (m_content2 != null ? m_content2.hashCode() : 0);
        return result;
    }
}
