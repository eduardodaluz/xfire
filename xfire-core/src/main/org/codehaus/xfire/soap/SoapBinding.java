package org.codehaus.xfire.soap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPFault;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.xml.namespace.QName;

import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.AbstractBinding;
import org.codehaus.xfire.service.binding.DocumentBinding;
import org.codehaus.xfire.service.binding.MessageBinding;
import org.codehaus.xfire.service.binding.RPCBinding;
import org.codehaus.xfire.service.binding.WrappedBinding;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.wsdl11.WSDL11Transport;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;

import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPBodyImpl;
import com.ibm.wsdl.extensions.soap.SOAPFaultImpl;
import com.ibm.wsdl.extensions.soap.SOAPHeaderImpl;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;

/**
 * A SOAP Binding which contains information on how SOAP is mapped to the service model.
 * @author Dan Diephouse
 */
public class SoapBinding extends Binding
{
    public static final String SOAP_BINDING_ID = "http://schemas.xmlsoap.org/wsdl/soap/";

    private String transportURI;
    private String style = SoapConstants.STYLE_DOCUMENT;
    private String use = SoapConstants.USE_LITERAL;
    
    private Map op2action = new HashMap();
    private Map action2Op = new HashMap();
    
    public SoapBinding(QName name, Service serviceInfo)
    {
        super(name, SOAP_BINDING_ID, serviceInfo);
    }

    public SoapBinding(QName name, String bindingId, Service serviceInfo)
    {
        super(name, bindingId, serviceInfo);
    }
    
    public String getStyle()
    {
        return style;
    }

    public String getStyle(OperationInfo operation)
    {
        return style;
    }
    
    public OperationInfo getOperationByAction(String action)
    {
        OperationInfo op = (OperationInfo) action2Op.get(action);
        
        if (op == null)
        {
            op = (OperationInfo) action2Op.get("*");
        }
        
        return op;
    }
    
    /**
     * Get the soap action for an operation. Will never return null.
     * @param operation
     * @return
     */
    public String getSoapAction(OperationInfo operation)
    {
        String action = (String) op2action.get(operation);
        
        if (action == null) action = "";
        
        return action;
    }
    
    public void setSoapAction(OperationInfo operation, String action)
    {
        op2action.put(operation, action);
        action2Op.put(action, operation);
    }
    
    public String getUse()
    {
        return use;
    }

    public void setStyle(String style)
    {
        this.style = style;
    }

    public void setUse(String use)
    {
        this.use = use;
    }

    public String getTransportURI()
    {
        return transportURI;
    }

    public void setTransportURI(String transportURI)
    {
        this.transportURI = transportURI;
    }

    public javax.wsdl.Binding createBinding(WSDLBuilder builder, PortType portType)
    {
        Transport t = getTransport();
        if (!(t instanceof WSDL11Transport)) return null;
        
        Definition def = builder.getDefinition();
        javax.wsdl.Binding wbinding = def.createBinding(); 

        wbinding.setQName( getName() );
        wbinding.setPortType( portType );
        wbinding.setUndefined(false);
        
        // add in soap:Body, et al
        wbinding.addExtensibilityElement(createSoapBinding());
        
        for (Iterator oitr = getService().getServiceInfo().getOperations().iterator(); oitr.hasNext();)
        {
            OperationInfo op = (OperationInfo) oitr.next();

            String inName = op.getInputMessage().getName().getLocalPart();
            String outName = null;
            if (op.hasOutput())
                outName = op.getOutputMessage().getName().getLocalPart();
            
            javax.wsdl.Operation wsdlOp = 
                (javax.wsdl.Operation) portType.getOperation(op.getName(), null, null);

            javax.wsdl.BindingOperation bop = createBindingOperation(builder, wsdlOp, op);

            createHeaders(builder, op, bop);
            
            wbinding.addBindingOperation(bop);
        }

        def.addBinding(wbinding);
        
        return wbinding;
    }

    protected javax.wsdl.BindingOperation createBindingOperation(WSDLBuilder builder, 
                                                                 Operation wsdlOp,
                                                                 OperationInfo op)
    {
        Definition def = builder.getDefinition();
        javax.wsdl.BindingOperation wbindOp = def.createBindingOperation();

        SOAPBody body = createSoapBody(builder.getService());

        SOAPOperationImpl soapOp = new SOAPOperationImpl();
        soapOp.setSoapActionURI(getSoapAction(op));
        
        BindingInput bindIn = def.createBindingInput();
        bindIn.setName( op.getInputMessage().getName().getLocalPart() );
        bindIn.addExtensibilityElement( body );
        wbindOp.setBindingInput( bindIn );
        
        if (wsdlOp.getOutput() != null)
        {
            BindingOutput bindOut = builder.getDefinition().createBindingOutput();
            bindOut.setName( wsdlOp.getOutput().getName() );
            bindOut.addExtensibilityElement( body );
            wbindOp.setBindingOutput( bindOut );
        }
        
        Map faults = wsdlOp.getFaults();
        if (faults != null)
        {
            for (Iterator itr = faults.values().iterator(); itr.hasNext();)
            {
                Fault fault = (Fault) itr.next();
                
                BindingFault bindingFault = def.createBindingFault();
                bindingFault.setName(fault.getName());
                
                SOAPFault soapFault = createSoapFault(builder.getService());
                soapFault.setName(fault.getName());

                bindingFault.addExtensibilityElement(soapFault);
                wbindOp.addBindingFault(bindingFault);
            }
        }
        
        wbindOp.setName( wsdlOp.getName() );
        wbindOp.setOperation( wsdlOp );
        wbindOp.addExtensibilityElement( soapOp );
        
        return wbindOp;
    }
    
    protected void createHeaders(WSDLBuilder builder, OperationInfo op, BindingOperation bop)
    {
        List inputHeaders = getHeaders(op.getInputMessage()).getMessageParts();
        
        if (inputHeaders.size() == 0)
        {
            return;
        }

        BindingInput bindingInput = bop.getBindingInput();

        Message reqHeaders = createHeaderMessages(builder, op.getInputMessage(), inputHeaders);
        builder.getDefinition().addMessage(reqHeaders);

        for (Iterator headerItr = reqHeaders.getParts().values().iterator(); headerItr.hasNext();)
        {
            Part headerInfo = (Part) headerItr.next();

            SOAPHeader soapHeader = new SOAPHeaderImpl();
            soapHeader.setMessage(reqHeaders.getQName());
            soapHeader.setPart(headerInfo.getName());
            soapHeader.setUse(getUse());

            bindingInput.addExtensibilityElement(soapHeader);
        }
    }

    protected Message createHeaderMessages(WSDLBuilder builder, MessageInfo msgInfo, List headers)
    {
        Message msg = builder.getDefinition().createMessage();

        msg.setQName(new QName(builder.getTargetNamespace(), 
                               msgInfo.getName().getLocalPart() + "Headers"));
        msg.setUndefined(false);

        for (Iterator itr = headers.iterator(); itr.hasNext();)
        {
            MessagePartInfo header = (MessagePartInfo) itr.next();

            Part part = builder.createPart(header);

            msg.addPart(part);
        }

        return msg;
    }

    protected SOAPFault createSoapFault( Service endpoint )
    {
        SOAPFault fault = new SOAPFaultImpl();
        fault.setUse( use ); 

        if ( getStyle().equals( SoapConstants.STYLE_RPC ) )
        {
            fault.setNamespaceURI( endpoint.getTargetNamespace() );
        }
        
        if ( use.equals( SoapConstants.USE_ENCODED ) )
        {
            List encodingStyles = new ArrayList();
            encodingStyles.add( endpoint.getSoapVersion().getSoapEncodingStyle() );
            
            fault.setEncodingStyles(encodingStyles);
        }
        
        return fault;
    }
    
    protected SOAPHeader createSoapHeader( Service endpoint )
    {
        SOAPHeader header = new SOAPHeaderImpl();
        header.setUse( use ); 

        if ( getStyle().equals( SoapConstants.STYLE_RPC ) )
        {
            header.setNamespaceURI( endpoint.getTargetNamespace() );
        }
        
        if ( use.equals( SoapConstants.USE_ENCODED ) )
        {
            List encodingStyles = new ArrayList();
            encodingStyles.add( endpoint.getSoapVersion().getSoapEncodingStyle() );
            
            header.setEncodingStyles(encodingStyles);
        }

        return header;
    }
    
    protected SOAPBinding createSoapBinding()
    {
        SOAPBinding soapBind = new SOAPBindingImpl();

        String style = getStyle();
        if (style.equals(SoapConstants.STYLE_WRAPPED)) style = SoapConstants.STYLE_DOCUMENT;
        
        soapBind.setStyle( style );
        soapBind.setTransportURI( getTransport().getSupportedBindings()[0] );

        return soapBind;
    }

    protected SOAPBody createSoapBody(Service service)
    {
        SOAPBody body = new SOAPBodyImpl();
        body.setUse( use ); 

        if ( getStyle().equals( SoapConstants.STYLE_RPC ) )
        {
            body.setNamespaceURI( service.getTargetNamespace() );
        }
        
        if ( use.equals( SoapConstants.USE_ENCODED ) )
        {
            List encodingStyles = new ArrayList();
            encodingStyles.add( service.getSoapVersion().getSoapEncodingStyle() );
            
            body.setEncodingStyles(encodingStyles);
        }
        
        return body;
    }

    public Port createPort(Endpoint endpoint, WSDLBuilder builder, javax.wsdl.Binding wbinding)
    {
        SOAPAddressImpl add = new SOAPAddressImpl();
        add.setLocationURI(endpoint.getAddress());
        
        Port port = builder.getDefinition().createPort();
        port.setBinding( wbinding );
        port.setName( endpoint.getName().getLocalPart() );
        port.addExtensibilityElement( add );
       
        return port;
    }
    
    public Port createPort(WSDLBuilder builder, javax.wsdl.Binding wbinding)
    {
        Transport t = getTransport();
        if (!(t instanceof WSDL11Transport)) return null;
        
        WSDL11Transport transport = (WSDL11Transport) t;
        
        SOAPAddressImpl add = new SOAPAddressImpl();
        add.setLocationURI(transport.getServiceURL(builder.getService()));
        
        Port port = builder.getDefinition().createPort();
        port.setBinding( wbinding );
        port.setName( builder.getService().getSimpleName() + transport.getName() + "Port" );
        port.addExtensibilityElement( add );
       
        return port;
    }

    public static AbstractBinding getSerializer(String style, String use)
    {
        if (style.equals(SoapConstants.STYLE_WRAPPED) && use.equals(SoapConstants.USE_LITERAL))
        {
            return new WrappedBinding();
        }
        else if (style.equals(SoapConstants.STYLE_DOCUMENT)
                && use.equals(SoapConstants.USE_LITERAL))
        {
            return new DocumentBinding();
        }
        else if (style.equals(SoapConstants.STYLE_RPC) && use.equals(SoapConstants.USE_LITERAL))
        {
            return new RPCBinding();
        }
        else if (style.equals(SoapConstants.STYLE_RPC) && use.equals(SoapConstants.USE_ENCODED))
        {
            return new RPCBinding();
        }
        else if (style.equals(SoapConstants.STYLE_MESSAGE) && use.equals(SoapConstants.USE_LITERAL))
        {
            return new MessageBinding();
        }
        else
        {
            throw new UnsupportedOperationException("Service style/use not supported: " + style
                    + "/" + use);
        }
    }
}
