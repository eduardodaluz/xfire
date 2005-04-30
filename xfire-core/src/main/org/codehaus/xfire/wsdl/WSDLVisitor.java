package org.codehaus.xfire.wsdl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.Visitor;
import org.codehaus.xfire.service.binding.Binding;

/**
 * Implementation of the <code>Visitor</code> interface that creates a WSDL from a <code>ServiceEndpoint</code>. After
 * visiting the endpoint, the created definition can be obtained using {@link #getDefinition()}.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class WSDLVisitor
        implements Visitor
{
    private Definition definition;
    private PortType currentPortType;
    private Operation currentOperation;
    private List operations = new ArrayList();
    private Message currentMessage;
    private Part currentPart;
    private javax.wsdl.Binding currentBinding;

    /**
     * Initializes a new instance of the <code>WSDLVisitor</code> class. Creates its own WSDL definition.
     */
    public WSDLVisitor()
    {
        try
        {
            WSDLFactory factory = WSDLFactory.newInstance();
            definition = factory.newDefinition();
        }
        catch (WSDLException e)
        {
            throw new WSDLCreationException("Could not create WSDLFactory", e);
        }
    }

    /**
     * Initializes a new instance of the <code>WSDLVisitor</code> class which fills the given WSDL definition.
     *
     * @param definition the definition to fill.
     */
    public WSDLVisitor(Definition definition)
    {
        this.definition = definition;
    }


    public void startEndpoint(ServiceEndpoint endpoint)
    {
    }

    public void endEndpoint(ServiceEndpoint endpoint)
    {
        Service service = definition.createService();
        Port port = definition.createPort();
        port.setName(endpoint.getService().getName().getLocalPart() + "Port");
        port.setBinding(currentBinding);
        endpoint.getBinding().populateWSDLPort(definition, port);
        service.addPort(port);
        definition.addService(service);
        currentPortType = null;
        currentBinding = null;
    }

    /**
     * Visits the given service.
     *
     * @param serviceInfo the service.
     */
    public void startService(ServiceInfo serviceInfo)
    {
        currentPortType = definition.createPortType();
        currentPortType.setQName(serviceInfo.getName());
        currentPortType.setUndefined(false);
        definition.addNamespace("tns", serviceInfo.getName().getNamespaceURI());
    }

    /**
     * Receive notatification of the end of a service visit.
     *
     * @param serviceInfo
     */
    public void endService(ServiceInfo serviceInfo)
    {
        definition.addPortType(currentPortType);
    }

    /**
     * Visits the given operation.
     *
     * @param operationInfo the operation.
     */
    public void startOperation(OperationInfo operationInfo)
    {
        currentOperation = definition.createOperation();
        currentOperation.setName(operationInfo.getName());
        if (operationInfo.isOneWay())
        {
            currentOperation.setStyle(OperationType.ONE_WAY);
        }
        else
        {
            currentOperation.setStyle(OperationType.SOLICIT_RESPONSE);
        }
        currentOperation.setUndefined(false);
    }

    /**
     * Receive notification at the end of a operation visit.
     *
     * @param operationInfo the operation.
     */
    public void endOperation(OperationInfo operationInfo)
    {
        currentPortType.addOperation(currentOperation);
        operations.add(currentOperation);
        currentOperation = null;
    }

    /**
     * Visits the given message.
     *
     * @param messageInfo the message.
     */
    public void startMessage(MessageInfo messageInfo)
    {
        currentMessage = definition.createMessage();
        currentMessage.setQName(messageInfo.getName());
        currentMessage.setUndefined(false);
    }

    /**
     * Receive notification at the end of a message visit.
     *
     * @param messageInfo the message.
     */
    public void endMessage(MessageInfo messageInfo)
    {
        if (messageInfo == messageInfo.getOperation().getInputMessage())
        {
            Input input = definition.createInput();
            input.setMessage(currentMessage);
            input.setName(currentMessage.getQName().getLocalPart());
            currentOperation.setInput(input);
        }
        else if (messageInfo == messageInfo.getOperation().getOutputMessage())
        {
            Output output = definition.createOutput();
            output.setMessage(currentMessage);
            output.setName(currentMessage.getQName().getLocalPart());
            currentOperation.setOutput(output);
        }
        definition.addMessage(currentMessage);
        currentMessage = null;
    }

    /**
     * Visits the given fault.
     *
     * @param faultInfo the fault.
     */
    public void startFault(FaultInfo faultInfo)
    {
        currentMessage = definition.createMessage();
        currentMessage.setQName(new QName(faultInfo.getName()));
        currentMessage.setUndefined(false);
        definition.addMessage(currentMessage);
        Fault fault = definition.createFault();
        fault.setName(faultInfo.getName());
        fault.setMessage(currentMessage);
        currentOperation.addFault(fault);
    }

    /**
     * Receive notification at the end of a fault visit.
     *
     * @param faultInfo the fault.
     */
    public void endFault(FaultInfo faultInfo)
    {
    }

    /**
     * Visits the given message part info.
     *
     * @param messagePartInfo the message part info.
     */
    public void startMessagePart(MessagePartInfo messagePartInfo)
    {
        currentPart = definition.createPart();
        currentPart.setName(messagePartInfo.getName().getLocalPart());
    }

    /**
     * Receive notification at the end of a message part visit.
     *
     * @param messagePartInfo the message part info.
     */
    public void endMessagePart(MessagePartInfo messagePartInfo)
    {
        currentMessage.addPart(currentPart);
    }

    public void startBinding(Binding binding)
    {
        currentBinding = definition.createBinding();
        currentBinding.setQName(binding.getName());
        currentBinding.setUndefined(false);
        currentBinding.setPortType(currentPortType);

        binding.populateWSDLBinding(definition, currentBinding);
        for (Iterator iterator = operations.iterator(); iterator.hasNext();)
        {
            Operation operation = (Operation) iterator.next();
            BindingOperation bindingOperation = definition.createBindingOperation();
            bindingOperation.setOperation(operation);
            bindingOperation.setName(operation.getName());
            binding.populateWSDLBindingOperation(definition, bindingOperation);

            if (operation.getInput() != null)
            {
                BindingInput bindingInput = definition.createBindingInput();
                bindingInput.setName(operation.getInput().getName());
                bindingOperation.setBindingInput(bindingInput);
                binding.populateWSDLBindingInput(definition, bindingInput);
            }
            if (operation.getOutput() != null)
            {
                BindingOutput bindingOutput = definition.createBindingOutput();
                bindingOutput.setName(operation.getOutput().getName());
                bindingOperation.setBindingOutput(bindingOutput);
                binding.populateWSDLBindingOutput(definition, bindingOutput);

            }
            if (!operation.getFaults().isEmpty())
            {
                for (Iterator faultIterator = operation.getFaults().values().iterator(); faultIterator.hasNext();)
                {
                    Fault fault = (Fault) faultIterator.next();
                    BindingFault bindingFault = definition.createBindingFault();
                    bindingFault.setName(fault.getName());
                    bindingOperation.addBindingFault(bindingFault);
                    binding.populateWSDLBindingFault(definition, bindingFault);
                }
            }
            currentBinding.addBindingOperation(bindingOperation);

        }
    }

    public void endBinding(Binding binding)
    {
        definition.addBinding(currentBinding);
    }


    public Definition getDefinition()
    {
        return definition;
    }
}
