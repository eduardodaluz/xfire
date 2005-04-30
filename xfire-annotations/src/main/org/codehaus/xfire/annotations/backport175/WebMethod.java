package org.codehaus.xfire.annotations.backport175;

/**
 * backport175 version of the WebService Annotation.
 *
 * @author Arjen Poutsma
 * @see org.codehaus.xfire.annotations.WebMethodAnnotation
 */
public interface WebMethod
{
    /**
     * Returns the action for this operation. For SOAP bindings, this determines the value of the SOAPAction header.
     *
     * @return the action for this operation.
     */
    String action();

    /**
     * Returns the name of the wsdl:operation matching this method. By default the WSDL operation name will be the same
     * as the Java method name.
     *
     * @return the name of the wsdl:operation matching this method.
     */
    String operationName();
}
