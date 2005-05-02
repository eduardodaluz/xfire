package org.codehaus.xfire.service.binding;

import javax.xml.namespace.QName;

import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.soap.SoapVersion;

/**
 * Factory for <code>SOAPBinding</code> implementations.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class SOAPBindingFactory
{
    private SOAPBindingFactory()
    {
    }

    /**
     * Creates a new instance of a <code>SOAPBinding</code> with the given parameters. The returned binding will use
     * SOAP version 1.1.
     *
     * @param name  the binding name.
     * @param style the binding style.
     * @param use   the binding use.
     * @return the created binding.
     * @throws IllegalArgumentException if the <code>style</code> <code>use</code> combination is invalid.
     */
    public static SOAPBinding createSOAPBinding(QName name, String style, String use)
    {
        return createSOAPBinding(name, style, use, Soap11.getInstance());
    }

    /**
     * Creates a new instance of a <code>SOAPBinding</code> with the given parameters.
     *
     * @param name    the binding name.
     * @param style   the binding style.
     * @param use     the binding use.
     * @param version the soap version.
     * @return the created binding.
     * @throws IllegalArgumentException if the <code>style</code> <code>use</code> combination is invalid.
     */
    public static SOAPBinding createSOAPBinding(QName name, String style, String use, SoapVersion version)
    {
        if (style.equals(SoapConstants.STYLE_DOCUMENT) && use.equals(SoapConstants.USE_LITERAL))
        {
            return createDocumentBinding(name, version);
        }
        else if (style.equals(SoapConstants.STYLE_WRAPPED) && use.equals(SoapConstants.USE_LITERAL))
        {
            return createWrappedBinding(name, version);
        }
        else if (style.equals(SoapConstants.STYLE_RPC) && use.equals(SoapConstants.USE_ENCODED))
        {
            return createRPCEncodedBinding(name, version);
        }
        else
        {
            throw new IllegalArgumentException("Illegal style/use combination [" + style + "/" + use + "]");
        }
    }


    /**
     * Creates a new document binding.
     *
     * @param name the binding name.
     * @return the created binding.
     */
    public static SOAPBinding createDocumentBinding(QName name)
    {
        return createDocumentBinding(name, Soap11.getInstance());
    }

    /**
     * Creates a new document binding.
     *
     * @param name    the binding name.
     * @param version the soap version.
     * @return the created binding.
     */
    public static SOAPBinding createDocumentBinding(QName name, SoapVersion version)
    {
        return new DocumentBinding(name, version);
    }

    /**
     * Creates a new wrapped binding. The returned binding will use SOAP version 1.1.
     *
     * @param name the binding name.
     * @return the created binding.
     */
    public static SOAPBinding createWrappedBinding(QName name)
    {
        return createWrappedBinding(name, Soap11.getInstance());

    }

    /**
     * Creates a new wrapped binding.
     *
     * @param name    the binding name.
     * @param version the soap version.
     * @return the created binding.
     */
    public static SOAPBinding createWrappedBinding(QName name, SoapVersion version)
    {
        return new WrappedBinding(name, version);
    }

    /**
     * Creates a new RPC encoded binding. The returned binding will use SOAP version 1.1.
     *
     * @param name the binding name.
     * @return the created binding.
     */
    public static SOAPBinding createRPCEncodedBinding(QName name)
    {
        return createRPCEncodedBinding(name, Soap11.getInstance());
    }

    /**
     * Creates a new RPC encoded binding.
     *
     * @param name    the binding name.
     * @param version the soap version.
     * @return the created binding.
     */
    public static SOAPBinding createRPCEncodedBinding(QName name, SoapVersion version)
    {
        return new RPCEncodedBinding(name, version);
    }


}
