package org.codehaus.xfire.annotations.backport175.soap;

/**
 * @author Arjen Poutsma
 */
public interface SOAPBinding
{
    /**
     * Returns the SOAP binding style, which defines the encoding style for messages send to and from the Web Service.
     * The returned value is one of {@link org.codehaus.xfire.annotations.soap.SOAPBindingAnnotation#STYLE_DOCUMENT} or
     * {@link org.codehaus.xfire.annotations.soap.SOAPBindingAnnotation#STYLE_RPC}.
     *
     * @return the SOAP binding style.
     */
    int style();

    /**
     * Returns the SOAP binding use, which defines the formatting style for messages sent to and from the Web Service.
     * The returned value is one of {@link org.codehaus.xfire.annotations.soap.SOAPBindingAnnotation#USE_LITERAL} or
     * {@link org.codehaus.xfire.annotations.soap.SOAPBindingAnnotation#USE_ENCODED}.
     *
     * @return the SOAP binding use.
     */
    int use();

    /**
     * Returns the SOAP parameter binding style. This style determines whether method parameters represent the entire
     * message body, or whether the parameters are elements wrapped inside a top-level element named after the
     * operation.
     * <p/>
     * The returned value is one of {@link org.codehaus.xfire.annotations.soap.SOAPBindingAnnotation#PARAMETER_STYLE_BARE}
     * or {@link org.codehaus.xfire.annotations.soap.SOAPBindingAnnotation#PARAMETER_STYLE_WRAPPED}.
     *
     * @return the SOAP parameter binding style.
     */
    int parameterStyle();
}
