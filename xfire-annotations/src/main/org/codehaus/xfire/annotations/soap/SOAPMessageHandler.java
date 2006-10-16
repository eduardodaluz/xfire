package org.codehaus.xfire.annotations.soap;

import java.io.Serializable;

/**
 * Represents a common implementation of the SOAP message handler. Specifies a single SOAP message handler.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class SOAPMessageHandler implements Serializable
{
    private String className;
    private String name;
    private InitParam[] initParams = new InitParam[0];
    private String[] roles = new String[0];
    private String[] headers = new String[0];

    /**
     * Initializes a new instance of the <code>SOAPMessageHandler</code>.
     *
     * @param className the name of the handler class.
     */
    public SOAPMessageHandler(String className)
    {
        this.className = className;
    }

    /**
     * Returns the name of the handler class.
     *
     * @return the name of the handler class.
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * Returns the name of the handler. Defaults to the name of the handler class.
     *
     * @return the name of the handler
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the handler.
     *
     * @param name the name of the handler
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the array of name/value pairs that should be passed to the handler during initialization.
     *
     * @return the initialization array
     */
    public InitParam[] getInitParams()
    {
        return initParams;
    }

    /**
     * Sets the array of name/value pairs that should be passed to the handler during initialization.
     *
     * @param initParams the initialization array
     */
    public void setInitParams(InitParam[] initParams)
    {
        this.initParams = initParams;
    }

    /**
     * Returns the list of SOAP roles/actors implemented by the handler
     *
     * @return the list of SOAP roles/actors
     */
    public String[] getRoles()
    {
        return roles;
    }

    /**
     * Sets the list of SOAP roles/actors implemented by the handler
     *
     * @param roles the list of SOAP roles/actors
     */
    public void setRoles(String[] roles)
    {
        this.roles = roles;
    }

    /**
     * Returns the list of SOAP headers processed by the handler. Each element in this array contains a {@link
     * javax.xml.namespace.QName} which defines the header element processed by the handler. The QNames are specified
     * using the string notation described in the documentation for {@link javax.xml.namespace.QName#valueOf(String)}.
     *
     * @return the list of SOAP headers
     */
    public String[] getHeaders()
    {
        return headers;
    }

    /**
     * Sets the list of SOAP headers processed by the handler. Each element in this array contains a {@link
     * javax.xml.namespace.QName} which defines the header element processed by the handler. The QNames are specified
     * using the string notation described in the documentation for {@link javax.xml.namespace.QName#valueOf(String)}.
     *
     * @param headers the list of SOAP headers
     */
    public void setHeaders(String[] headers)
    {
        this.headers = headers;
    }


}
