/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.xfire.annotations.soap;

import org.codehaus.xfire.soap.SoapConstants;

/**
 * Represents an common representation of a soap binding annotation. Specifies the mapping of the Web Service onto the
 * SOAP message protocol.
 *
 * @author Arjen Poutsma
 */
public class SOAPBindingAnnotation
{
    private int style = STYLE_DOCUMENT;
    private int use = USE_LITERAL;
    private int parameterStyle = PARAMETER_STYLE_WRAPPED;
    /**
     * Constant used to specify a document binding style. This is the default document style.
     */
    public final static int STYLE_DOCUMENT = 0;
    /**
     * Constant used to specify a rpc binding style.
     */
    public final static int STYLE_RPC = 1;

    /**
     * Constant used to specify a literal binding use. This is the default use.
     */
    public final static int USE_LITERAL = 0;
    /**
     * Constant used to specify a encoded binding use.
     */
    public final static int USE_ENCODED = 1;

    /**
     * Constant used to specify a bare parameter binding style.
     */
    public final static int PARAMETER_STYLE_BARE = 0;
    /**
     * Constant used to specify a wrapped parameter binding style. This is the default parameter style.
     */
    public final static int PARAMETER_STYLE_WRAPPED = 1;

    /**
     * Returns the SOAP binding style, which defines the encoding style for messages send to and from the Web Service.
     * The returned value is one of {@link #STYLE_DOCUMENT} or {@link #STYLE_RPC}.
     *
     * @return the SOAP binding style.
     */
    public int getStyle()
    {
        return style;
    }

    /**
     * Returns a <code>String</code> representation of the SOAP binding style. The returned value is one of {@link
     * SoapConstants#STYLE_DOCUMENT} or {@link SoapConstants#STYLE_RPC}.
     *
     * @return the SOAP binding style as a <code>String</code>.
     * @see SoapConstants
     */
    public String getStyleString()
    {
        if (style == SOAPBindingAnnotation.STYLE_DOCUMENT)
        {
            return SoapConstants.STYLE_DOCUMENT;
        }
        else if (style == SOAPBindingAnnotation.STYLE_RPC)
        {
            return SoapConstants.STYLE_RPC;
        }
        else
        {
            throw new IllegalStateException("Invalid style set: " + style);
        }
    }

    /**
     * Sets the SOAP binding style, which defines the encoding style for messages send to and from the Web Service. The
     * given parameter must be one of {@link #STYLE_DOCUMENT} or {@link #STYLE_RPC}.
     *
     * @param style the new binding style.
     * @throws IllegalArgumentException if <code>style</code> is not a valid style.
     */
    public void setStyle(int style)
    {
        if (style == SOAPBindingAnnotation.STYLE_DOCUMENT || style == SOAPBindingAnnotation.STYLE_RPC)
        {
            this.style = style;
        }
        else
        {
            throw new IllegalArgumentException("Invalid style: " + style);
        }
    }

    /**
     * Returns the SOAP binding use, which defines the formatting style for messages sent to and from the Web Service.
     * The returned value is one of {@link #USE_LITERAL} or {@link #USE_ENCODED}.
     *
     * @return the SOAP binding use.
     */
    public int getUse()
    {
        return use;
    }

    /**
     * Returns a <code>String</code> representation of the SOAP binding use. The returned value is one of {@link
     * SoapConstants#USE_ENCODED} or {@link SoapConstants#USE_LITERAL}.
     *
     * @return the SOAP binding use as a <code>String</code>.
     * @see SoapConstants
     */
    public String getUseString()
    {
        if (use == SOAPBindingAnnotation.USE_ENCODED)
        {
            return SoapConstants.USE_ENCODED;
        }
        else if (use == SOAPBindingAnnotation.USE_LITERAL)
        {
            return SoapConstants.USE_LITERAL;
        }
        else
        {
            throw new IllegalStateException("Invalid use set: " + use);
        }

    }

    /**
     * Sets the SOAP binding use, which defines the formatting style for messages sent to and from the Web Service. The
     * given parameter must be one of {@link #USE_LITERAL} or {@link #USE_ENCODED}.
     *
     * @param use the new binding use.
     * @throws IllegalArgumentException if <code>use</code> is not a valid use.
     */
    public void setUse(int use)
    {
        if (use == SOAPBindingAnnotation.USE_ENCODED || use == SOAPBindingAnnotation.USE_LITERAL)
        {
            this.use = use;
        }
        else
        {
            throw new IllegalArgumentException("Invalid use: " + use);
        }
    }

    /**
     * Returns the SOAP parameter binding style. This style determines whether method parameters represent the entire
     * message body, or whether the parameters are elements wrapped inside a top-level element named after the
     * operation.
     * <p/>
     * The returned value is one of {@link #PARAMETER_STYLE_BARE} or {@link #PARAMETER_STYLE_WRAPPED}.
     *
     * @return the SOAP parameter binding style.
     */
    public int getParameterStyle()
    {
        return parameterStyle;
    }

    /**
     * Sets the SOAP parameter binding style. This style determines whether method parameters represent the entire
     * message body, or whether the parameters are elements wrapped inside a top-level element named after the
     * operation
     * <p/>
     * The given parameter must be one of {@link #PARAMETER_STYLE_BARE} or {@link #PARAMETER_STYLE_WRAPPED}.
     *
     * @param parameterStyle the new SOAP parameter binding style.
     * @throws IllegalArgumentException if <code>parameterStyle</code> is not a valid parameter style.
     */
    public void setParameterStyle(int parameterStyle)
    {
        if (parameterStyle == SOAPBindingAnnotation.PARAMETER_STYLE_BARE ||
                parameterStyle == SOAPBindingAnnotation.PARAMETER_STYLE_WRAPPED)
        {
            this.parameterStyle = parameterStyle;
        }
        else
        {
            throw new IllegalArgumentException("Invalid parameter style: " + parameterStyle);
        }
    }
}
