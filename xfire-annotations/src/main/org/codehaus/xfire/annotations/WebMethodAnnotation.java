/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.xfire.annotations;

/**
 * Represents an common representation of a web method attribute. Specifies that the given method is exposed as a Web
 * Service operation, making it part of the Web Service�s public contract. A WebMethod annotation is required for each
 * method that is published by the Web Service.
 *
 * @author Arjen Poutsma
 */
public class WebMethodAnnotation
{
    private String action;
    private String operationName;

    /**
     * Returns the action for this operation. For SOAP bindings, this determines the value of the SOAPAction header.
     *
     * @return the action for this operation.
     */
    public String getAction()
    {
        return action;
    }

    /**
     * Sets the action for this operation. For SOAP bindings, this determines the value of the SOAPAction header.
     *
     * @param action the new action for this operation.
     */
    public void setAction(String action)
    {
        this.action = action;
    }

    /**
     * Returns the name of the wsdl:operation matching this method. By default the WSDL operation name will be the same
     * as the Java method name.
     *
     * @return the name of the wsdl:operation matching this method.
     */
    public String getOperationName()
    {
        return operationName;
    }

    /**
     * Sets the name of the wsdl:operation matching this method. By default the WSDL operation name will be the same as
     * the Java method name.
     *
     * @param operationName the new name of the wsdl:operation matching this method.
     */
    public void setOperationName(String operationName)
    {
        this.operationName = operationName;
    }

    /**
     * Returns a <code>String</code> representation of this <code>WebMethodAnnotation</code>.
     *
     * @return a string representation.
     */
    public String toString()
    {
        return "WebMethod{" +
                "action='" + action + "'" +
                ", operationName='" + operationName + "'" +
                "}";
    }
}
