package org.codehaus.xfire.session;

/**
 * Session
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface Session
{
    /**
     * Get a variable from the session by the key.
     * 
     * @param key
     * @return
     */
    Object get( Object key );
    
    /**
     * Put a variable into the session with a key.
     * 
     * @param key
     * @param value
     */
    void put( Object key, Object value );
}
