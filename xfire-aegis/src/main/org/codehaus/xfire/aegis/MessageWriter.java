package org.codehaus.xfire.aegis;

import javax.xml.namespace.QName;

/**
 * Writes messages to an output stream.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface MessageWriter
{
    void writeValue( Object value );

    void writeValueAsInt( Integer i );

    void writeValueAsDouble(Double double1);

    void writeValueAsLong(Long l);

    void writeValueAsFloat(Float f);

    void writeValueAsShort(Short short1);
    
    void writeValueAsBoolean(boolean b);
 
    MessageWriter getAttributeWriter(String name);

    MessageWriter getAttributeWriter(String name, String namespace);
    
    MessageWriter getAttributeWriter(QName qname);

    MessageWriter getElementWriter(String name);

    MessageWriter getElementWriter(String name, String namespace);
    
    MessageWriter getElementWriter(QName qname);

    String getPrefixForNamespace( String namespace );

    /**
     * Get a prefix for a namespace. After calling this, the prefix returned is registered with the namespace.
     * <p/>
     * This method will make an attempt to use the hint prefix if possible. If the namespace is already registered or
     * the hint is already registered with a different namespace then the behavior will be the same as the non-hint
     * version.
     *
     * @param namespace     the namespace to retrieve the prefix for
     * @param hint          the hint for the prefix.
     * @return      the prefix associated with the namespace
     */
    String getPrefixForNamespace( String namespace, String hint );
   
    /**
     * Tells the MessageWriter that writing operations are completed so 
     * it can write the end element.
     */
    void close();

    void writeXsiNil();
}
