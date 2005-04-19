package org.codehaus.xfire.aegis;

import java.util.Calendar;
import java.util.Date;

import javax.xml.namespace.QName;

/**
 * A MessageReader. You must call getNextChildReader() until hasMoreChildReaders()
 * returns false.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface MessageReader
{
    public String getValue();

    public Calendar getValueAsCalendar();

    public int getValueAsInt();

    public long getValueAsLong();

    public double getValueAsDouble();

    public float getValueAsFloat();

	public boolean getValueAsBoolean();

    public Date getValueAsDate();

    //public Date getValueAsTime();
    
    public Date getValueAsDateTime();
    
    public boolean hasMoreAttributeReaders();
    
    public MessageReader getNextAttributeReader();
    
    public boolean hasMoreElementReaders();
    
    public MessageReader getNextElementReader();
    
    public QName getName();
    
    /**
     * Get the local name of the element this reader represents.
     * @return
     */
    public String getLocalName();

    /**
     * @return
     */
    public String getNamespace();

}
