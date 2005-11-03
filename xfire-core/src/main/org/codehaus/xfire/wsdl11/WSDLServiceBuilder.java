package org.codehaus.xfire.wsdl11;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.SoapFaultSerializer;
import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.ObjectBindingFactory;
import org.xml.sax.InputSource;

public class WSDLServiceBuilder
    extends WSDLVisitor
{
    private Service service;
    private ServiceInfo serviceInfo;
    private OperationInfo opInfo;
    private String style;
    private String use;
    
    private List services = new ArrayList();
    
    private BindingProvider provider;
    
    public WSDLServiceBuilder(Definition definition)
    {
        super(definition);
    }

    public WSDLServiceBuilder(InputStream is) throws WSDLException
    {
        super(WSDLFactory.newInstance().newWSDLReader().readWSDL(null, new InputSource(is)));
    }

    public Collection getServices()
    {
        return services;
    }
    
    protected void visit(PortType portType)
    {
        super.visit(portType);
    }
    
    protected void visit(Binding binding)
    {
        SOAPBinding soapBinding = getSOAPBinding(binding);
        
        style = soapBinding.getStyle();
    }

    protected void visit(BindingFault bindingFault, Fault fault)
    {
        FaultInfo faultInfo = opInfo.addFault(fault.getName());
        
        
    }

    protected void visit(BindingInput bindingInput, Input input)
    {
        MessageInfo info = opInfo.createMessage(input.getMessage().getQName());
        
        opInfo.setInputMessage(info);
        
        Map parts = input.getMessage().getParts();
        for (Iterator itr = parts.values().iterator(); itr.hasNext();)
        {
            Part entry = (Part) itr.next();
            
            QName typeName = entry.getTypeName();
            if (typeName != null)
            {
                
            }

            QName elementName = entry.getElementName();
            if (elementName != null)
            {
                
            }
            
            
        }
        
        SOAPBody body = getSOAPBody(bindingInput.getExtensibilityElements());
        if (body != null)
        {
            checkUse(body.getUse());
            
            use = body.getUse();
        }
    }

    private void checkUse(String use2)
    {
        if (use == null) return;
        
        if (!use.equals(use2))
            throw new XFireRuntimeException("Multiple uses not supported.");
    }

    protected void visit(BindingOutput bindingOutput, Output output)
    {
        MessageInfo info = opInfo.createMessage(output.getMessage().getQName());
        
        opInfo.setOutputMessage(info);
        
        SOAPBody body = getSOAPBody(bindingOutput.getExtensibilityElements());
        if (body != null)
        {
            checkUse(body.getUse());
            
            use = body.getUse();
        }
    }

    protected void visit(BindingOperation operation)
    {
        opInfo = serviceInfo.addOperation(operation.getName(), null);
    }

    protected void begin(javax.wsdl.Service wservice)
    {
        serviceInfo = new ServiceInfo(wservice.getQName(), Object.class);
        
        service = new Service(serviceInfo);
    }

    protected void end(javax.wsdl.Service wservice)
    {
        service.setBinding(ObjectBindingFactory.getMessageBinding(style, use));
        service.setFaultSerializer(new SoapFaultSerializer());
        
        services.add(service);
    }
    
    protected void visit(javax.wsdl.Port port)
    {
        SOAPAddress add = getSOAPAddress(port);
        SOAPBinding sbind = getSOAPBinding(port.getBinding());
        
        Endpoint ep = new Endpoint(new QName(getDefinition().getTargetNamespace(), port.getName()), 
                                   add.getLocationURI(), 
                                   sbind.getTransportURI());
        
        service.addEndpoint(ep);
    }
}
