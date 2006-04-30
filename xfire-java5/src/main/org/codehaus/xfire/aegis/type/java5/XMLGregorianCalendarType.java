package org.codehaus.xfire.aegis.type.java5;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;

/**
 * @author Dan Diephouse
 */
public class XMLGregorianCalendarType extends Type
{
    private DatatypeFactory dtFactory;
    
    public XMLGregorianCalendarType()
    {
        try
        {
            dtFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e)
        {
            throw new XFireRuntimeException("Couldn't load DatatypeFactory.", e);
        }
        
        setTypeClass(XMLGregorianCalendar.class);
    }

    @Override
    public Object readObject(MessageReader reader, MessageContext context)
        throws XFireFault
    {
        return dtFactory.newXMLGregorianCalendar(reader.getValue());
    }

    @Override
    public void writeObject(Object object, MessageWriter writer, MessageContext context)
        throws XFireFault
    {
        writer.writeValue(((XMLGregorianCalendar) object).toXMLFormat());
    }
}
