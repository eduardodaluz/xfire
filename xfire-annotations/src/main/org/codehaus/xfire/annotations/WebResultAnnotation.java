package org.codehaus.xfire.annotations;

/**
 * Represents an common representation of a web result annotation. Customizes the mapping of the return value to a WSDL
 * part and XML element.
 *
 * @author Arjen Poutsma
 */
public class WebResultAnnotation
{
    private String name;
    private String targetNameSpace;

    /**
     * Returns the name of the return value as it appears in the WSDL and messages on the wire. For RPC bindings, this
     * is the name of the wsdl:part representing the return value. For document bindings, this is the local name of the
     * XML element representing the return value.
     *
     * @return the name of the return value.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of return value as it appears in the WSDL and messages on the wire. For RPC bindings, this is the
     * name of  the wsdl:part representing the return value. For document bindings, this is the local name of the XML
     * element  representing the return value.
     *
     * @param name the new name of the return value.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the XML namespace for the return value. Only used with document bindings, where the return value maps to
     * an  XML element. Defaults to the targetNamespace of the Web Service.
     *
     * @return the XML namespace for the return value.
     */
    public String getTargetNameSpace()
    {
        return targetNameSpace;
    }

    /**
     * Sets the XML namespace for the return value. Only used with document bindings, where the return value maps to an
     * XML element. Defaults to the targetNamespace of the Web Service.
     *
     * @param targetNameSpace the new XML namespace for the return value.
     */
    public void setTargetNameSpace(String targetNameSpace)
    {
        this.targetNameSpace = targetNameSpace;
    }

    /**
     * Returns a String representation of this <code>WebResultAnnotation</code> attribute.
     *
     * @return a string representation.
     */
    public String toString()
    {
        return "WebResultAnnotation{" +
                "name='" + name + "'" +
                ", targetNameSpace='" + targetNameSpace + "'" +
                "}";
    }
}
