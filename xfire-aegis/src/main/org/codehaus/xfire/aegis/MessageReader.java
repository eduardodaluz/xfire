package org.codehaus.xfire.aegis;

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

    public int getValueAsInt();

    public long getValueAsLong();

    public double getValueAsDouble();

    public float getValueAsFloat();

	public boolean getValueAsBoolean();
    
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
