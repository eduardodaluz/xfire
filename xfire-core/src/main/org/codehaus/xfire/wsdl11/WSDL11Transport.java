package org.codehaus.xfire.wsdl11;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;

import org.codehaus.xfire.service.ServiceEndpoint;

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
    Binding createBinding(PortType portType, ServiceEndpoint service);

    /**
     * @param transportBinding
     * @return
     */
    Port createPort(Binding transportBinding, ServiceEndpoint service);

    /**
     * @param portType
     * @param wsdlOp
     * @param service
     * @return
     */
    BindingOperation createBindingOperation(PortType portType, Operation wsdlOp, ServiceEndpoint service);
}
