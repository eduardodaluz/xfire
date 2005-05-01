package org.codehaus.xfire.service.binding;

import javax.wsdl.Definition;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.soap.SoapVersion;

/**
 * Represents a SOAP Document style binding, used by a <code>ServiceEndpoint</code>.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class DocumentBinding
        extends SOAPBinding
{
    /**
     * Initializes a new instance of the <code>DocumentBinding</code> class with the given qualified name. The {@link
     * #getSoapVersion() soap version} is set to <code>1.1</code>.
     *
     * @param name the qualified name.
     */
    public DocumentBinding(QName name)
    {
        this(name, Soap11.getInstance());
    }

    /**
     * Initializes a new instance of the <code>DocumentBinding</code> class with the given qualified name and soap
     * version.
     *
     * @param name    the name.
     * @param version the soap version.
     */
    public DocumentBinding(QName name, SoapVersion version)
    {
        super(name, version);
    }

    /**
     * Returns the SOAP binding style used by this binding. This class always returns {@link
     * org.codehaus.xfire.soap.SoapConstants#STYLE_DOCUMENT}.
     *
     * @return {@link org.codehaus.xfire.soap.SoapConstants#STYLE_DOCUMENT}
     */
    public String getStyle()
    {
        return SoapConstants.STYLE_DOCUMENT;
    }

    /**
     * Returns the SOAP binding use used by this binding. This class always returns {@link
     * org.codehaus.xfire.soap.SoapConstants#USE_LITERAL}.
     *
     * @return {@link org.codehaus.xfire.soap.SoapConstants#USE_LITERAL}
     */
    public final String getUse()
    {
        return SoapConstants.USE_LITERAL;
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
        // TODO: move the code from org.codehaus.xfire.wsdl11.builder.DocumentWSDL that generates XSD elements here
        QName elementName = new QName(part.getName());
        part.setElementName(elementName);
    }
}
