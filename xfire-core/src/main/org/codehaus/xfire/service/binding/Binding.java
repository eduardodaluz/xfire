package org.codehaus.xfire.service.binding;

import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.xml.namespace.QName;

import org.codehaus.xfire.service.Visitable;
import org.codehaus.xfire.service.Visitor;

/**
 * Forms the abstract base class for <code>ServiceEndpoint</code> bindings.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public abstract class Binding
        implements Visitable
{
    private QName name;

    /**
     * Initializes a new <code>Binding</code> with the given qualified name.
     *
     * @param name the qualified name.
     */
    protected Binding(QName name)
    {
        this.name = name;
    }

    /**
     * Accepts the given visitor.
     *
     * @param visitor the visitor.
     */
    public final void accept(Visitor visitor)
    {
        visitor.startBinding(this);
        visitor.endBinding(this);
    }

    /**
     * Allows subclasses to customize the given WSDL binding.
     *
     * @param definition  the definition.
     * @param wsdlBinding the WSDL binding.
     */
    public void populateWSDLBinding(Definition definition, javax.wsdl.Binding wsdlBinding)
    {
    }

    /**
     * Allows subclasses to customize the given WSDL binding fault.
     *
     * @param definition   the definition.
     * @param bindingFault the WSDL binding fault.
     */
    public void populateWSDLBindingFault(Definition definition, BindingFault bindingFault)
    {
    }

    /**
     * Allows subclasses to customize the given WSDL binding input.
     *
     * @param definition   the definition.
     * @param bindingInput the WSDL binding input.
     */
    public void populateWSDLBindingInput(Definition definition, BindingInput bindingInput)
    {
    }

    /**
     * Allows subclasses to customize the given WSDL binding operation
     *
     * @param definition       the definition.
     * @param bindingOperation the WSDL binding operation
     */
    public void populateWSDLBindingOperation(Definition definition, BindingOperation bindingOperation)
    {
    }

    /**
     * Allows subclasses to customize the given WSDL binding output.
     *
     * @param definition    the definition.
     * @param bindingOutput the WSDL binding output.
     */
    public void populateWSDLBindingOutput(Definition definition, BindingOutput bindingOutput)
    {
    }

    /**
     * Allows subclasses to customize the giben WSDL port.
     *
     * @param definition the definition.
     * @param port       the WSDL port.
     */
    public void populateWSDLPort(Definition definition, Port port)
    {
    }

    /**
     * Returns the qualified name for this binding.
     *
     * @return the qualified name.
     */
    public QName getName()
    {
        return name;
    }

    /**
     * Sets the qualified name for this binding.
     *
     * @param name the qualified name.
     */
    public void setName(QName name)
    {
        this.name = name;
    }
}

