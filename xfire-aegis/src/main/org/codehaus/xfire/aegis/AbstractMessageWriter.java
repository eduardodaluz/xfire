package org.codehaus.xfire.aegis;

import org.codehaus.xfire.soap.SoapConstants;

/**
 * Basic type conversion functionality for writing messages.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public abstract class AbstractMessageWriter
        implements MessageWriter
{
    /**
     * Create a LiteralWriter but without writing an element name.
     *
     * @param writer
     */
    public AbstractMessageWriter()
    {
    }

    public void writeXsiNil()
    {
        MessageWriter attWriter = getAttributeWriter("nil", SoapConstants.XSI_NS);
        attWriter.writeValue("true");
        attWriter.close();
    }
    
    /**
     * @see org.codehaus.xfire.aegis.MessageWriter#writeValueAsInt(java.lang.Integer)
     */
    public void writeValueAsInt(Integer i)
    {
        writeValue(i.toString());
    }

    /**
     * @see org.codehaus.xfire.aegis.MessageWriter#writeValueAsDouble(java.lang.Double)
     */
    public void writeValueAsDouble(Double d)
    {
        writeValue(d.toString());
    }

    /**
     * @see org.codehaus.xfire.aegis.MessageWriter#writeValueAsCharacter(java.lang.Character)
     */
    public void writeValueAsCharacter(Character char1)
    {
        writeValue(char1.toString());
    }
    
    /**
     * @see org.codehaus.xfire.aegis.MessageWriter#writeValueAsLong(java.lang.Long)
     */
    public void writeValueAsLong(Long l)
    {
        writeValue(l.toString());
    }

    /**
     * @see org.codehaus.xfire.aegis.MessageWriter#writeValueAsFloat(java.lang.Float)
     */
    public void writeValueAsFloat(Float f)
    {
        writeValue(f.toString());
    }

    /**
     * @see org.codehaus.xfire.aegis.MessageWriter#writeValueAsBoolean(boolean)
     */
    public void writeValueAsBoolean(boolean b)
    {
        writeValue(b ? "true" : "false");
    }

    public void writeValueAsShort(Short s)
    {
        writeValue(s.toString());
    }
}
