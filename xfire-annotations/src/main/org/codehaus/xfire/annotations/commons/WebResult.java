package org.codehaus.xfire.annotations.commons;

import org.codehaus.xfire.annotations.WebResultAnnotation;

/**
 * Commons Attributes version of the WebResult Annotation.
 *
 * @author Arjen Poutsma
 * @@org.apache.commons.attributes.Target(org.apache.commons.attributes.Target.RETURN)
 * @see org.codehaus.xfire.annotations.WebResultAnnotation
 */
public class WebResult
        extends WebResultAnnotation
{
    /**
     * Initializes a new instance of the <code>WebResult</code> attribute.
     */
    public WebResult()
    {
    }

    /**
     * Initializes a new instance of the <code>WebResult</code> attribute with the specified name.
     *
     * @param name the name.
     */
    public WebResult(String name)
    {
        setName(name);
    }

}
