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

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl11.WSDL11ParameterBinding;
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
public abstract class AbstractWSDLTransport
    extends AbstractTransport
    implements WSDL11Transport
{
    public abstract String getServiceURL(Service service);

    public abstract String getTransportURI(Service service);

     /**
     * @see org.codehaus.xfire.transport.Transport#createBinding(javax.wsdl.PortType)
     */
    public Binding createBinding(PortType portType, Service service,  WSDL11ParameterBinding paramBinding)
    {
        Binding binding = new BindingImpl();
        binding.setQName( new QName( service.getServiceInfo().getName().getNamespaceURI(),
                                     service.getServiceInfo().getName().getLocalPart() + getName() + "Binding" ) );
        binding.setPortType( portType );
        binding.setUndefined(false);
        
        binding.addExtensibilityElement( createSoapBinding(service, paramBinding) );

        return binding;
    }

    protected SOAPBinding createSoapBinding(Service endpoint, WSDL11ParameterBinding binding)
    {
        SOAPBinding soapBind = new SOAPBindingImpl();

        String style = binding.getStyle();
        if ( style.equals( SoapConstants.STYLE_WRAPPED ) )
            style = SoapConstants.STYLE_DOCUMENT;
        
        soapBind.setStyle( style );
        soapBind.setTransportURI( getTransportURI(endpoint) );

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
    public BindingOperation createBindingOperation(PortType portType, 
                                                   Operation wsdlOp, 
                                                   Service service,
                                                   WSDL11ParameterBinding binding)
    {
        BindingOperation bindOp = new BindingOperationImpl();
        
        // Create bindings
        SOAPBody body = createSoapBody( service , binding);

        SOAPOperationImpl soapOp = new SOAPOperationImpl();
        soapOp.setSoapActionURI("");
        
        BindingInput bindIn = new BindingInputImpl();
        bindIn.setName( wsdlOp.getInput().getName() );
        bindIn.addExtensibilityElement( body );
        
        if (wsdlOp.getOutput() != null)
        {
            BindingOutput bindOut = new BindingOutputImpl();
            bindOut.setName( wsdlOp.getOutput().getName() );
            bindOut.addExtensibilityElement( body );
            bindOp.setBindingOutput( bindOut );
        }
        
        bindOp.setName( wsdlOp.getName() );
        bindOp.setOperation( wsdlOp );
        bindOp.setBindingInput( bindIn );
        bindOp.addExtensibilityElement( soapOp );
        
        return bindOp;
    }
    
    public SOAPBody createSoapBody( Service endpoint, WSDL11ParameterBinding binding )
    {
        SOAPBody body = new SOAPBodyImpl();
        body.setUse( binding.getUse() ); 

        if ( binding.getStyle().equals( SoapConstants.STYLE_RPC ) )
        {
            body.setNamespaceURI( endpoint.getServiceInfo().getName().getNamespaceURI() );
        }
        
        if ( binding.getUse().equals( SoapConstants.USE_ENCODED ) )
        {
            List encodingStyles = new ArrayList();
            encodingStyles.add( endpoint.getSoapVersion().getSoapEncodingStyle() );
            
            body.setEncodingStyles(encodingStyles);
        }
        
        return body;
    }
}
