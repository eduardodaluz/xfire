package org.codehaus.xfire.annotations.commons;

import org.codehaus.xfire.annotations.WebParamAnnotation;

/**
 * Commons Attributes version of the WebParam Annotation.
 *
 * @author Arjen Poutsma
 * @@org.apache.commons.attributes.Target(org.apache.commons.attributes.Target.METHOD_PARAMETER)
 * @see org.codehaus.xfire.annotations.WebParamAnnotation
 */
public class WebParam
        extends WebParamAnnotation
{
    public WebParam()
    {
    }

    public WebParam(String name)
    {
        setName(name);
    }

    /**
     * Initializes a new instance of the <code>WebParam</code> attribute with the specified name
     * and namespace.
     *
     * @param name the name.
     * @param targetNamespace the target namespace.
     */
    public WebParam(String name, String targetNamespace)
    {
        setName(name);
        setTargetNamespace(targetNamespace);
    }
}
