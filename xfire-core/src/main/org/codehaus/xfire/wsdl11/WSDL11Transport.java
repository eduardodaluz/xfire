package org.codehaus.xfire.wsdl11;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;

import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;

/**
 * Indicates that a particular transport supports WSDL 1.1 generation.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface WSDL11Transport
{
    /**
     * @param portType
     * @param service
     */
    Binding createBinding(WSDLBuilder builder, PortType portType, WSDL11ParameterBinding binding);

    /**
     * @param transportBinding
     * @return
     */
    Port createPort(WSDLBuilder builder, Binding transportBinding);

    /**
     * @param op TODO
     * @param portType
     * @param wsdlOp
     * @param service
     * @return
     */
    BindingOperation createBindingOperation(WSDLBuilder builder,
                                            OperationInfo op,
                                            PortType portType,
                                            Operation wsdlOp, WSDL11ParameterBinding binding);
}
