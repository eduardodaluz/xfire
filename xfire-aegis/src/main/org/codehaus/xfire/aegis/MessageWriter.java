package org.codehaus.xfire.aegis;

import java.util.Calendar;
import java.util.Date;

import javax.xml.namespace.QName;

/**
 * Writes messages to an output stream.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface MessageWriter
{
    void writeValue( Object value );

    void writeValueAsCalendar( Calendar calendar );

    void writeValueAsDate( Date date );

    void writeValueAsDateTime( Date date );
    //void writeValueAsTime( Date date );

    void writeValueAsInt( Integer i );

    void writeValueAsDouble(Double double1);

    void writeValueAsLong(Long l);

    void writeValueAsFloat(Float f);

    void writeValueAsBoolean(boolean b);
 
    MessageWriter getAttributeWriter(String name);

    MessageWriter getAttributeWriter(String name, String namespace);
    
    MessageWriter getAttributeWriter(QName qname);

    MessageWriter getElementWriter(String name);

    MessageWriter getElementWriter(String name, String namespace);
    
    MessageWriter getElementWriter(QName qname);
   
    /**
     * Tells the MessageWriter that writing operations are completed so 
     * it can write the end element.
     */
    void close();
}
