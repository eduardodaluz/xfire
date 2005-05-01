package org.codehaus.xfire.service.binding;

import javax.wsdl.Definition;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.soap.SoapVersion;

/**
 * Represents a SOAP RPC encoded binding, used by a <code>ServiceEndpoint</code>.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class RPCEncodedBinding
        extends SOAPBinding
{
    /**
     * Initializes a new instance of the <code>RPCEncodedBinding</code> class with the given qualified name. The {@link
     * #getSoapVersion() soap version} is set to <code>1.1</code>.
     *
     * @param name the qualified name.
     */
    public RPCEncodedBinding(QName name)
    {
        this(name, Soap11.getInstance());

    }

    /**
     * Initializes a new instance of the <code>RPCEncodedBinding</code> class with the given qualified name and soap
     * version.
     *
     * @param name    the name.
     * @param version the soap version.
     */
    public RPCEncodedBinding(QName name, SoapVersion version)
    {
        super(name, version);
    }

    /**
     * Returns the SOAP binding style used by this binding. This class always returns {@link SoapConstants#STYLE_RPC}.
     *
     * @return {@link SoapConstants#STYLE_RPC}
     */
    public String getStyle()
    {
        return SoapConstants.STYLE_RPC;
    }

    /**
     * Returns the SOAP binding use used by this binding. This class always returns {@link
     * org.codehaus.xfire.soap.SoapConstants#USE_ENCODED}.
     *
     * @return {@link SoapConstants#USE_ENCODED}.
     */
    public String getUse()
    {
        return SoapConstants.USE_ENCODED;
    }

    /**
     * Populates the given WSDL part with a WSDL SOAP binding.
     *
     * @param definition the definition.
     * @param part       the WSDL message part.
     * @param typeClass  the class represented by the part.
     * @see org.codehaus.xfire.wsdl11.builder.DocumentWSDL
     */
    public void populateWSDLPart(Definition definition, Part part, Class typeClass)
    {
        // TODO: move the code from org.codehaus.xfire.wsdl11.builder.RPCEncodedWSDL that generates XSD types here
        QName typeName = new QName(part.getName());
        part.setElementName(typeName);
    }
}
