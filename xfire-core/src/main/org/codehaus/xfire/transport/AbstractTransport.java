package org.codehaus.xfire.transport;

import java.util.ArrayList;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.xml.namespace.QName;

import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl11.WSDL11Transport;

import com.ibm.wsdl.BindingImpl;
import com.ibm.wsdl.BindingInputImpl;
import com.ibm.wsdl.BindingOperationImpl;
import com.ibm.wsdl.BindingOutputImpl;
import com.ibm.wsdl.PortImpl;
import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPBodyImpl;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Dec 21, 2004
 */
public abstract class AbstractTransport
    implements Transport, WSDL11Transport
{
    private HandlerPipeline requestPipeline;
    private HandlerPipeline responsePipeline;
    private FaultHandlerPipeline faultPipeline;

    public abstract String getServiceURL(Service service);

    public abstract String getTransportURI(Service service);

     /**
     * @see org.codehaus.xfire.transport.Transport#createBinding(javax.wsdl.PortType)
     */
    public Binding createBinding(PortType portType, Service service)
    {
        Binding binding = new BindingImpl();
        binding.setQName( new QName( service.getDefaultNamespace(), 
                                     service.getName() + getName() + "Binding" ) );
        binding.setPortType( portType );
        binding.setUndefined(false);
        
        binding.addExtensibilityElement( createSoapBinding(service) );

        return binding;
    }

    protected SOAPBinding createSoapBinding(Service service)
    {
        SOAPBinding soapBind = new SOAPBindingImpl();
        
        String style = service.getStyle();
        if ( style.equals( SoapConstants.STYLE_WRAPPED ) )
            style = SoapConstants.STYLE_DOCUMENT;
        
        soapBind.setStyle( style );
        soapBind.setTransportURI( getTransportURI(service) );

        return soapBind;
    }

    /**
     * @see org.codehaus.xfire.transport.Transport#createPort(javax.wsdl.Binding)
     */
    public Port createPort(Binding transportBinding, Service service)
    {
        SOAPAddressImpl add = new SOAPAddressImpl();
        add.setLocationURI( getServiceURL( service ) );
        
        Port port = new PortImpl();
        port.setBinding( transportBinding );
        port.setName( service.getName() + getName() + "Port" );
        port.addExtensibilityElement( add );
       
        return port;
    }
   
    /**
     * @see org.codehaus.xfire.transport.Transport#createBindingOperation(javax.wsdl.Message, javax.wsdl.Message, org.codehaus.xfire.java.JavaService)
     */
    public BindingOperation createBindingOperation(PortType portType, Operation wsdlOp, Service service)
    {
        BindingOperation bindOp = new BindingOperationImpl();
        
        // Create bindings
        SOAPBody body = createSoapBody( service );

        SOAPOperationImpl soapOp = new SOAPOperationImpl();
        soapOp.setSoapActionURI("");
        
        BindingInput bindIn = new BindingInputImpl();
        bindIn.setName( wsdlOp.getInput().getName() );
        bindIn.addExtensibilityElement( body );
        
        BindingOutput bindOut = new BindingOutputImpl();
        bindOut.setName( wsdlOp.getOutput().getName() );
        bindOut.addExtensibilityElement( body );
        bindOp.setBindingOutput( bindOut );

        bindOp.setName( wsdlOp.getName() );
        bindOp.setOperation( wsdlOp );
        bindOp.setBindingInput( bindIn );
        bindOp.addExtensibilityElement( soapOp );
        
        return bindOp;
    }
    
    public SOAPBody createSoapBody( Service service )
    {
        SOAPBody body = new SOAPBodyImpl();
        body.setUse( service.getUse() ); 

        if ( service.getStyle().equals( SoapConstants.STYLE_RPC ) )
        {
            body.setNamespaceURI( service.getDefaultNamespace() );
        }
        
        if ( service.getUse().equals( SoapConstants.USE_ENCODED ) )
        {
            List encodingStyles = new ArrayList();
            encodingStyles.add( service.getSoapVersion().getSoapEncodingStyle() );
            
            body.setEncodingStyles(encodingStyles);
        }
        
        return body;
    }
    
    /**
     * @return Returns the faultPipeline.
     */
    public FaultHandlerPipeline getFaultPipeline()
    {
        return faultPipeline;
    }
    
    /**
     * @param faultPipeline The faultPipeline to set.
     */
    public void setFaultPipeline(FaultHandlerPipeline faultPipeline)
    {
        this.faultPipeline = faultPipeline;
    }
    
    /**
     * @return Returns the requestPipeline.
     */
    public HandlerPipeline getRequestPipeline()
    {
        return requestPipeline;
    }
    
    /**
     * @param requestPipeline The requestPipeline to set.
     */
    public void setRequestPipeline(HandlerPipeline requestPipeline)
    {
        this.requestPipeline = requestPipeline;
    }
    
    /**
     * @return Returns the responsePipeline.
     */
    public HandlerPipeline getResponsePipeline()
    {
        return responsePipeline;
    }
    
    /**
     * @param responsePipeline The responsePipeline to set.
     */
    public void setResponsePipeline(HandlerPipeline responsePipeline)
    {
        this.responsePipeline = responsePipeline;
    }
}
