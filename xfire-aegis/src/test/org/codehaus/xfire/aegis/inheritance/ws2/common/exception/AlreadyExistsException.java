package org.codehaus.xfire.aegis.inheritance.ws2.common.exception;

/**
 * <br/>
 * 
 * @author xfournet
 */
public class AlreadyExistsException
    extends Exception
{
    private String m_id;

    public AlreadyExistsException()
    {
    }

    public AlreadyExistsException(String id)
    {
        m_id = id;
    }

    public String getId()
    {
        return m_id;
    }

    public void setId(String id)
    {
        m_id = id;
    }

    public String toString()
    {
        return "[" + getClass().getName() + "] id=" + m_id;
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

        final AlreadyExistsException that = (AlreadyExistsException) o;

        if (getMessage() != null ? !getMessage().equals(that.getMessage())
                : that.getMessage() != null)
        {
            return false;
        }

        if (m_id != null ? !m_id.equals(that.m_id) : that.m_id != null)
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return (m_id != null ? m_id.hashCode() : 0);
    }
}
