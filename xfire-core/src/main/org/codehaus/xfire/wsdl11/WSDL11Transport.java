package org.codehaus.xfire.wsdl11;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;

import org.codehaus.xfire.service.Service;

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
    Binding createBinding(PortType portType, Service service, WSDL11ParameterBinding binding);

    /**
     * @param transportBinding
     * @return
     */
    Port createPort(Binding transportBinding, Service service);

    /**
     * @param portType
     * @param wsdlOp
     * @param service
     * @return
     */
    BindingOperation createBindingOperation(PortType portType,
                                            Operation wsdlOp,
                                            Service service,
                                            WSDL11ParameterBinding binding);
}
