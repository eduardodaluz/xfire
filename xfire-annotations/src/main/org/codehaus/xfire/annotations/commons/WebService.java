package org.codehaus.xfire.annotations.commons;

import org.codehaus.xfire.annotations.WebServiceAnnotation;

/**
 * Commons Attributes version of the WebService Annotation.
 *
 * @author Arjen Poutsma
 * @@org.apache.commons.attributes.Target(org.apache.commons.attributes.Target.CLASS)
 * @see org.codehaus.xfire.annotations.WebServiceAnnotation
 */
public class WebService
        extends WebServiceAnnotation
{

    /**
     * Initializes a new instance of the <code>WebService</code> attribute.
     */
    public WebService()
    {
    }

    /**
     * Initializes a new instance of the <code>WebService</code> attribute with the specified name.
     *
     * @param name the name of the Web Service.
     */
    public WebService(String name)
    {
        setName(name);
    }


}
