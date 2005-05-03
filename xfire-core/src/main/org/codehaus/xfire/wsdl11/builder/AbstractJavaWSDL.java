package org.codehaus.xfire.wsdl11.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Output;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.xfire.wsdl11.WSDL11Transport;

/**
 * WSDL
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public abstract class AbstractJavaWSDL
    extends org.codehaus.xfire.wsdl11.builder.AbstractWSDL
    implements WSDLWriter
{
    private PortType portType;

    private Binding binding;

    private Collection transports;

    private Map wsdlOps;

    public AbstractJavaWSDL(ServiceEndpoint service, Collection transports) throws WSDLException
    {
        super(service);

        this.transports = transports;

        wsdlOps = new HashMap();

        PortType portType = createAbstractInterface();

        createConcreteInterface(portType);

        writeDocument();
    }

    public PortType createAbstractInterface()
        throws WSDLException
    {
        ServiceEndpoint service = getService();
        Definition def = getDefinition();

        QName portName = new QName(getInfo().getTargetNamespace(), getInfo().getPortType());

        portType = def.createPortType();
        portType.setQName(portName);
        portType.setUndefined(false);
        def.addPortType(portType);

        // Create Abstract operations
        for (Iterator itr = service.getService().getOperations().iterator(); itr.hasNext();)
        {
            OperationInfo op = (OperationInfo) itr.next();
            Message req = getInputMessage(op);
            def.addMessage(req);

            Message res = null;
            if (!op.isOneWay())
            {
                res = getOutputMessage(op);
                def.addMessage(res);
            }

            javax.wsdl.Operation wsdlOp = createOperation(op, req, res);
            wsdlOp.setUndefined(false);
            portType.addOperation(wsdlOp);

            wsdlOps.put(op.getName(), wsdlOp);
        }

        return portType;
    }

    public void createConcreteInterface(PortType portType)
    {
        ServiceEndpoint service = getService();
        Definition def = getDefinition();

        QName name = new QName(getInfo().getTargetNamespace(), getInfo().getServiceName());

        // Create a concrete instance for each transport.
        javax.wsdl.Service wsdlService = def.createService();
        wsdlService.setQName(name);

        for (Iterator itr = transports.iterator(); itr.hasNext();)
        {
            Object transportObj = (Transport) itr.next();
            if (!(transportObj instanceof WSDL11Transport))
                break;

            WSDL11Transport transport = (WSDL11Transport) transportObj;

            Binding transportBinding = transport.createBinding(portType, service);

            for (Iterator oitr = service.getService().getOperations().iterator(); oitr.hasNext();)
            {
                // todo: move out of the first loop, we'll be creating req/res
                // multiple times otherwise
                OperationInfo op = (OperationInfo) oitr.next();

                javax.wsdl.Operation wsdlOp = (javax.wsdl.Operation) wsdlOps.get(op.getName());

                BindingOperation bop = transport.createBindingOperation(portType, wsdlOp, service);
                transportBinding.addBindingOperation(bop);
            }

            Port transportPort = transport.createPort(transportBinding, service);

            def.addBinding(transportBinding);
            wsdlService.addPort(transportPort);
        }

        def.addService(wsdlService);

    }

    private Message getOutputMessage(OperationInfo op)
    {
        // response message
        Message res = getDefinition().createMessage();
        res.setQName(new QName(getInfo().getTargetNamespace(), op.getName() + "Response"));

        res.setUndefined(false);

        createOutputParts(res, op);

        return res;
    }

    private Message getInputMessage(OperationInfo op)
    {
        Message req = getDefinition().createMessage();
        req.setQName(new QName(getInfo().getTargetNamespace(), op.getName() + "Request"));
        req.setUndefined(false);

        createInputParts(req, op);

        return req;
    }

    protected abstract void createInputParts(Message req, OperationInfo op);

    protected abstract void createOutputParts(Message req, OperationInfo op);

    public javax.wsdl.Operation createOperation(OperationInfo op, Message req, Message res)
    {
        Definition def = getDefinition();
        javax.wsdl.Operation wsdlOp = def.createOperation();

        Input input = def.createInput();
        input.setMessage(req);
        input.setName(req.getQName().getLocalPart());
        wsdlOp.setInput(input);

        if (res != null)
        {
            Output output = def.createOutput();
            output.setMessage(res);
            output.setName(res.getQName().getLocalPart());
            wsdlOp.setOutput(output);
        }

        wsdlOp.setName(op.getName());

        return wsdlOp;
    }
}
