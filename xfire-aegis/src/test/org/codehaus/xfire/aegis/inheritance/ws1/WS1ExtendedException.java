package org.codehaus.xfire.aegis.inheritance.ws1;

/**
 * <br/>
 * 
 * @author xfournet
 */
public class WS1ExtendedException
    extends WS1Exception
{
    private int m_extendedCode;

    public WS1ExtendedException()
    {
    }

    public WS1ExtendedException(String message)
    {
        super(message);
    }

    public WS1ExtendedException(String message, int errorCode1, int extendedCode)
    {
        super(message, errorCode1);
        m_extendedCode = extendedCode;
    }

    public int getExtendedCode()
    {
        return m_extendedCode;
    }

    public void setExtendedCode(int extendedCode)
    {
        m_extendedCode = extendedCode;
    }

    public String toString()
    {
        return super.toString() + "; extendedCode=" + m_extendedCode;
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

        final WS1ExtendedException that = (WS1ExtendedException) o;

        if (m_extendedCode != that.m_extendedCode)
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        int result = super.hashCode();
        result = 29 * result + m_extendedCode;
        return result;
    }
}
