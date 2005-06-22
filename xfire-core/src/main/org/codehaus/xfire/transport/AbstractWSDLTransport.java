package org.codehaus.xfire.transport;

import java.util.ArrayList;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.xml.namespace.QName;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl11.WSDL11ParameterBinding;
import org.codehaus.xfire.wsdl11.WSDL11Transport;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;

import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPBodyImpl;
import com.ibm.wsdl.extensions.soap.SOAPHeaderImpl;
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
    public Binding createBinding(WSDLBuilder builder, PortType portType, WSDL11ParameterBinding paramBinding)
    {
        Definition def = builder.getDefinition();
        ServiceInfo info = builder.getService().getServiceInfo();
        
        Binding binding = def.createBinding();
        binding.setQName( new QName( info.getName().getNamespaceURI(),
                                     info.getName().getLocalPart() + getName() + "Binding" ) );
        binding.setPortType( portType );
        binding.setUndefined(false);
        
        binding.addExtensibilityElement( createSoapBinding(builder.getService(), paramBinding) );

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
    public Port createPort(WSDLBuilder builder, Binding transportBinding)
    {
        Definition def = builder.getDefinition();
        Service service = builder.getService();
        
        SOAPAddressImpl add = new SOAPAddressImpl();
        add.setLocationURI( getServiceURL( service ) );
        
        Port port = def.createPort();
        port.setBinding( transportBinding );
        port.setName( service.getName() + getName() + "Port" );
        port.addExtensibilityElement( add );
       
        return port;
    }
   
    /**
     * @see org.codehaus.xfire.transport.Transport#createBindingOperation(javax.wsdl.Message, javax.wsdl.Message, org.codehaus.xfire.java.JavaService)
     */
    public BindingOperation createBindingOperation(WSDLBuilder builder,
                                                   PortType portType, 
                                                   Operation wsdlOp, 
                                                   WSDL11ParameterBinding binding)
    {
        Definition def = builder.getDefinition();
        BindingOperation bindOp = def.createBindingOperation();
        
        // Create bindings
        SOAPBody body = createSoapBody(builder.getService(), binding);

        SOAPOperationImpl soapOp = new SOAPOperationImpl();
        soapOp.setSoapActionURI("");
        
        BindingInput bindIn = def.createBindingInput();
        bindIn.setName( wsdlOp.getInput().getName() );
        bindIn.addExtensibilityElement( body );
        
        if (wsdlOp.getOutput() != null)
        {
            BindingOutput bindOut = def.createBindingOutput();
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
    
    public SOAPHeader createSoapHeader( Service endpoint, WSDL11ParameterBinding binding )
    {
        SOAPHeader header = new SOAPHeaderImpl();
        header.setUse( binding.getUse() ); 

        if ( binding.getStyle().equals( SoapConstants.STYLE_RPC ) )
        {
            header.setNamespaceURI( endpoint.getServiceInfo().getName().getNamespaceURI() );
        }
        
        if ( binding.getUse().equals( SoapConstants.USE_ENCODED ) )
        {
            List encodingStyles = new ArrayList();
            encodingStyles.add( endpoint.getSoapVersion().getSoapEncodingStyle() );
            
            header.setEncodingStyles(encodingStyles);
        }

        return header;
    }
}
