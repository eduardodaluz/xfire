package org.codehaus.xfire.annotations;

/**
 * Represents a common representation of a web parameter annotation. Customizes the mapping of an individual parameter
 * to a Web Service message part and XML element.
 *
 * @author Arjen Poutsma
 */
public class WebParamAnnotation
{
    private String name;
    private String targetNamespace;
    private boolean header;
    private int mode = MODE_IN;

    /**
     * Constant used to specify that a parameter flows inwards.
     */
    public final static int MODE_IN = 0;
    /**
     * Constant used to specify that a parameter flows both in and outwards.
     */
    public final static int MODE_INOUT = 1;
    /**
     * Constant used to specify that a parameter flows outwards.
     */
    public final static int MODE_OUT = 2;

    /**
     * Returns the name of the parameter as it appears in the WSDL. For RPC bindings, this is name of the wsdl:part
     * representing  the parameter. For document bindings, this is the local name of the XML element representing the
     * parameter.  Defaults to the name of the parameter as it appears in the argument list.
     *
     * @return the name of the parameter as it appears in the WSDL.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the parameter as it appears in the WSDL. For RPC bindings, this is name of the wsdl:part
     * representing  the parameter. For document bindings, this is the local name of the XML element representing the
     * parameter.  Defaults to the name of the parameter as it appears in the argument list.
     *
     * @param name the new name of the parameter as it appears in the WSDL.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the XML namespace for the parameter. Only used with document bindings, where the parameter maps to an XML
     * element. Defaults to the targetNamespace for the Web Service.
     *
     * @return the XML namespace for the parameter.
     */
    public String getTargetNamespace()
    {
        return targetNamespace;
    }

    /**
     * Sets the XML namespace for the parameter. Only used with document bindings, where the parameter maps to an XML
     * element. Defaults to the targetNamespace for the Web Service.
     *
     * @param targetNamespace the XML namespace for the parameter.
     */
    public void setTargetNamespace(String targetNamespace)
    {
        this.targetNamespace = targetNamespace;
    }

    /**
     * Returns the direction in which the parameter is flowing. One of {@link #MODE_IN}, {@link #MODE_OUT}, or {@link
     * #MODE_INOUT}.
     *
     * @return the parameter mode.
     */
    public int getMode()
    {
        return mode;
    }

    /**
     * Sets the direction in which the parameter is flowing. The given parameter must be  of {@link #MODE_IN}, {@link
     * #MODE_OUT}, or {@link #MODE_INOUT}.
     *
     * @param mode the new parameter mode.
     */
    public void setMode(int mode)
    {
        if (mode == WebParamAnnotation.MODE_IN || mode == WebParamAnnotation.MODE_INOUT ||
                mode == WebParamAnnotation.MODE_OUT)
        {
            this.mode = mode;
        }
        else
        {
            throw new IllegalArgumentException("Invalid mode " + mode);
        }
    }

    /**
     * If <code>true</code>, the parameter is pulled from a message header rather then the message body.
     *
     * @return <code>true</code> if a header; <code>false</code> otherwise.
     */
    public boolean isHeader()
    {
        return header;
    }

    /**
     * Determines whether this parameter is a header.
     *
     * @param header <code>true</code> if a header; <code>false</code> otherwise.
     */
    public void setHeader(boolean header)
    {
        this.header = header;
    }
}
