package org.codehaus.xfire.security;

/**
 * @author tomeks
 *
 */
public interface WSPasswordCallback
{
    String handle(String id,boolean hashed, int usg);

}
