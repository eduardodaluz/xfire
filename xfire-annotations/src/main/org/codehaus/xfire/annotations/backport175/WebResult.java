package org.codehaus.xfire.annotations.backport175;

/**
 * backport175 Attributes version of the WebResult Annotation.
 *
 * @author Arjen Poutsma
 * @see org.codehaus.xfire.annotations.WebResultAnnotation
 */
public interface WebResult
{
    /**
     * Returns the name of the return value as it appears in the WSDL and messages on the wire. For RPC bindings, this
     * is the name of the wsdl:part representing the return value. For document bindings, this is the local name of the
     * XML element representing the return value.
     *
     * @return the name of the return value.
     */
    String name();

    /**
     * Returns the XML namespace for the return value. Only used with document bindings, where the return value maps to
     * an  XML element. Defaults to the targetNamespace of the Web Service.
     *
     * @return the XML namespace for the return value.
     */
    String targetNameSpace();

}

