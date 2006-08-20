package org.codehaus.xfire.aegis.inheritance.ws1;

/**
 * <br/>
 * 
 * @author xfournet
 */
public class WS1Exception
    extends Exception
{
    private int m_errorCode;
    private Object simpleBean;
    
    public WS1Exception()
    {
    }

    public WS1Exception(String message)
    {
        super(message);
    }

    public WS1Exception(String message, int errorCode1)
    {
        super(message);
        m_errorCode = errorCode1;
    }

    public int getErrorCode()
    {
        return m_errorCode;
    }

    public void setErrorCode(int errorCode)
    {
        m_errorCode = errorCode;
    }

    public Object getSimpleBean()
    {
        return simpleBean;
    }

    public void setSimpleBean(Object simpleBean)
    {
        this.simpleBean = simpleBean;
    }

    public String toString()
    {
        return "[" + getClass().getName() + "] msg=" + getMessage() + "; errorCode=" + m_errorCode;
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

        final WS1Exception that = (WS1Exception) o;

        if (getMessage() != null ? !getMessage().equals(that.getMessage())
                : that.getMessage() != null)
        {
            return false;
        }

        if (m_errorCode != that.m_errorCode)
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return m_errorCode;
    }
}
