package org.codehaus.xfire.annotations.commons;

import org.codehaus.xfire.annotations.WebMethodAnnotation;

/**
 * Commons Attributes version of the WebMethod Annotation.
 *
 * @author Arjen Poutsma
 * @@org.apache.commons.attributes.Target(org.apache.commons.attributes.Target.METHOD)
 * @see org.codehaus.xfire.annotations.WebMethodAnnotation
 */
public class WebMethod
        extends WebMethodAnnotation
{

    /**
     * Initializes a new instance of the <code>WebMethod</code> attribute.
     */
    public WebMethod()
    {
    }

    /**
     * Initializes a new instance of the <code>WebMetod</code> attribute with the specified operation name.
     *
     * @param operationName the operation name.
     */
    public WebMethod(String operationName)
    {
        setOperationName(operationName);
    }

}
