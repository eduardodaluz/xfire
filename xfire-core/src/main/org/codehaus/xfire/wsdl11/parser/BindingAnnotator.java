package org.codehaus.xfire.wsdl11.parser;

import javax.wsdl.BindingFault;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Output;
import javax.wsdl.Port;

import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;

public abstract class BindingAnnotator
{
    private Service service;
    private Definition definition;
    private WSDLServiceBuilder serviceBuilder;

    public WSDLServiceBuilder getServiceBuilder()
    {
        return serviceBuilder;
    }

    public void setServiceBuilder(WSDLServiceBuilder serviceBuilder)
    {
        this.serviceBuilder = serviceBuilder;
    }

    public Service getService()
    {
        return service;
    }

    public void setService(Service service)
    {
        this.service = service;
    }

    public Definition getDefinition()
    {
        return definition;
    }

    public void setDefinition(Definition definition)
    {
        this.definition = definition;
    }

    protected abstract boolean isUnderstood(javax.wsdl.Binding binding);
    
    protected abstract Binding getBinding();

    protected void visit(BindingFault bindingFault, Fault fault, FaultInfo msg)
    {
    }

    protected void visit(javax.wsdl.BindingOutput bindingOutput, Output output, MessageInfo msg)
    {
    }

    protected void visit(javax.wsdl.BindingInput bindingInput, Input input, MessageInfo msg)
    {
    }

    protected void visit(javax.wsdl.BindingOperation operation, OperationInfo opInfo)
    {
    }
    
    protected void visit(javax.wsdl.Binding wbinding)
    {
    }

    protected void visit(Port port)
    {
    }
}
