package org.codehaus.xfire.transport;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import org.codehaus.xfire.service.Service;

/**
 * Transport
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface Transport
{
    String getName();

    /**
     * @param portType
     * @param service
     */
    Binding createBinding(PortType portType, Service service);

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
    BindingOperation createBindingOperation(PortType portType, Operation wsdlOp, Service service);
}
