package org.codehaus.xfire.service;

import org.codehaus.yom.Element;

/**
 * Throws an exception while echoing.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public class BadEcho
{
    public Element echo(Element e) 
        throws Exception
    {
        throw new Exception("Fault!");
    }
}