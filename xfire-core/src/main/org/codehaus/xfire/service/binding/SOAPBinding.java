package org.codehaus.xfire.service.binding;


import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPFault;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.namespace.QName;

import org.codehaus.xfire.service.transport.Transport;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.wsdl.WSDLCreationException;

/**
 * Represents the abstract base class for SOAP bindings, used by a <code>ServiceEndpoint</code>.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public abstract class SOAPBinding
        extends Binding
{
    /**
     * Constant that defines the WSDL SOAP namespace.
     */
    public static final String WSDL_SOAP_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap/";
    /**
     * Constant that defines the WSDL SOAP prefix used to register the SOAP namespace.
     */
    public static final String WSDL_SOAP_PREFIX = "soap";
    private SoapVersion soapVersion;

    /**
     * Initializes a new instance of the <code>SOAPBinding</code> class with the given qualified name and soap version.
     *
     * @param name    the name.
     * @param version the soap version.
     */
    protected SOAPBinding(QName name, SoapVersion version)
    {
        super(name);
        this.soapVersion = version;
    }

    /**
     * Populates the given WSDL binding with a WSDL SOAP binding.
     *
     * @param definition  the definition used.
     * @param wsdlBinding the WSDL binding to populate.
     */
    public void populateWSDLBinding(Definition definition, javax.wsdl.Binding wsdlBinding, Transport transport)
    {
        definition.addNamespace(WSDL_SOAP_PREFIX, WSDL_SOAP_NAMESPACE);
        try
        {
            javax.wsdl.extensions.soap.SOAPBinding soapBinding =
                    (javax.wsdl.extensions.soap.SOAPBinding) definition.getExtensionRegistry().createExtension(
                            javax.wsdl.Binding.class,
                            new QName(WSDL_SOAP_NAMESPACE, "binding"));
            soapBinding.setStyle(getStyle());
            soapBinding.setTransportURI(transport.getTransportURI());
            wsdlBinding.addExtensibilityElement(soapBinding);
        }
        catch (WSDLException e)
        {
            throw new WSDLCreationException("Could not create SOAPBinding", e);
        }
    }

    /**
     * Populates the given WSDL binding fault with a WSDL SOAP fault.
     *
     * @param definition   the definition used.
     * @param bindingFault the WSDL binding fault to populate.
     */
    public void populateWSDLBindingFault(Definition definition, BindingFault bindingFault)
    {
        definition.addNamespace(WSDL_SOAP_PREFIX, WSDL_SOAP_NAMESPACE);

        try
        {
            SOAPFault soapFault =
                    (SOAPFault) definition.getExtensionRegistry().createExtension(BindingFault.class,
                                                                                  new QName(WSDL_SOAP_NAMESPACE,
                                                                                            "fault"));
            soapFault.setUse(getUse());
            bindingFault.addExtensibilityElement(soapFault);
        }
        catch (WSDLException e)
        {
            throw new WSDLCreationException("Could not create SOAPFault", e);
        }
    }

    /**
     * Populates the given WSDL binding input with a WSDL SOAP body.
     *
     * @param definition   the definition used.
     * @param bindingInput the WSDL binding input to populate.
     */
    public void populateWSDLBindingInput(Definition definition, BindingInput bindingInput)
    {
        definition.addNamespace(WSDL_SOAP_PREFIX, WSDL_SOAP_NAMESPACE);
        try
        {
            SOAPBody soapBody =
                    (SOAPBody) definition.getExtensionRegistry().createExtension(BindingInput.class,
                                                                                 new QName(WSDL_SOAP_NAMESPACE,
                                                                                           "body"));
            soapBody.setUse(getUse());
            bindingInput.addExtensibilityElement(soapBody);
        }
        catch (WSDLException e)
        {
            throw new WSDLCreationException("Could not create SOAPBody", e);
        }
    }

    /**
     * Populates the given WSDL binding operation with a WSDL SOAP operation.
     *
     * @param definition       the definition used.
     * @param bindingOperation the WSDL binding input to populate.
     */
    public void populateWSDLBindingOperation(Definition definition, BindingOperation bindingOperation)
    {
        definition.addNamespace(WSDL_SOAP_PREFIX, WSDL_SOAP_NAMESPACE);
        try
        {
            SOAPOperation soapOperation =
                    (SOAPOperation) definition.getExtensionRegistry().createExtension(BindingOperation.class,
                                                                                      new QName(WSDL_SOAP_NAMESPACE,
                                                                                                "operation"));
            soapOperation.setSoapActionURI("");
            bindingOperation.addExtensibilityElement(soapOperation);
        }
        catch (WSDLException e)
        {
            throw new WSDLCreationException("Could not create SOAPOperation", e);
        }
    }

    /**
     * Populates the given WSDL binding output with a WSDL SOAP body.
     *
     * @param definition    the definition used.
     * @param bindingOutput the WSDL binding output to populate.
     */
    public void populateWSDLBindingOutput(Definition definition, BindingOutput bindingOutput)
    {
        definition.addNamespace(WSDL_SOAP_PREFIX, WSDL_SOAP_NAMESPACE);
        try
        {
            SOAPBody soapBody =
                    (SOAPBody) definition.getExtensionRegistry().createExtension(BindingOutput.class,
                                                                                 new QName(WSDL_SOAP_NAMESPACE,
                                                                                           "body"));
            soapBody.setUse(getUse());
            bindingOutput.addExtensibilityElement(soapBody);
        }
        catch (WSDLException e)
        {
            throw new WSDLCreationException("Could not create SOAPBody", e);
        }
    }

    /**
     * Populates the given WSDL port with a SOAP address.
     *
     * @param definition the definition used.
     * @param port       the WSDL port to populate.
     * @param transport  the tranport used when binding.
     */
    public void populateWSDLPort(Definition definition, Port port, Transport transport)
    {
        definition.addNamespace(WSDL_SOAP_PREFIX, WSDL_SOAP_NAMESPACE);
        try
        {
            SOAPAddress soapAddress =
                    (SOAPAddress) definition.getExtensionRegistry().createExtension(Port.class,
                                                                                    new QName(WSDL_SOAP_NAMESPACE,
                                                                                              "address"));
            soapAddress.setLocationURI(transport.getAddress());
            port.addExtensibilityElement(soapAddress);
        }
        catch (WSDLException e)
        {
            throw new WSDLCreationException("Could not create SOAPAddress", e);
        }
    }

    /**
     * Returns the SOAP version used by this binding.
     *
     * @return the SOAP version.
     */
    public SoapVersion getSoapVersion()
    {
        return soapVersion;
    }

    /**
     * Sets the SOAP version used by this binding.
     *
     * @param soapVersion the SOAP version.
     */
    public void setSoapVersion(SoapVersion soapVersion)
    {
        this.soapVersion = soapVersion;
    }

    /**
     * Returns the SOAP binding style used by this binding. The returned value is one of {@link
     * SoapConstants#STYLE_DOCUMENT} or {@link SoapConstants#STYLE_RPC}.
     *
     * @return the style used.
     */
    public abstract String getStyle();

    /**
     * Returns the SOAP binding use used by this binding. The returned value is one of {@link SoapConstants#USE_LITERAL}
     * or {@link SoapConstants#USE_ENCODED}.
     *
     * @return the use used.
     */
    public abstract String getUse();
}

