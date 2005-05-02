package org.codehaus.xfire.service.binding;

import javax.wsdl.Definition;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.soap.SoapVersion;

/**
 * Represents a SOAP wrapped style binding, used by a <code>ServiceEndpoint</code>.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
class WrappedBinding
        extends DocumentBinding
{
    /**
     * Initializes a new instance of the <code>WrappedBinding</code> class with the given qualified name and soap
     * version.
     *
     * @param name    the name.
     * @param version the soap version.
     */
    WrappedBinding(QName name, SoapVersion version)
    {
        super(name, version);
    }

    /**
     * Returns the SOAP binding style used by this binding. This class always returns {@link
     * org.codehaus.xfire.soap.SoapConstants#STYLE_WRAPPED}.
     *
     * @return {@link org.codehaus.xfire.soap.SoapConstants#STYLE_WRAPPED}
     */
    public String getStyle()
    {
        return SoapConstants.STYLE_WRAPPED;
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
        // TODO: move the code from org.codehaus.xfire.wsdl11.builder.WrappedWSDL that generates XSD elements here
    }
}
