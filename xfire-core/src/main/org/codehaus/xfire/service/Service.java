package org.codehaus.xfire.service;

import javax.wsdl.WSDLException;

import org.codehaus.xfire.fault.FaultHandler;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.wsdl.WSDL;

/**
 * A service descriptor. This class must be thread safe.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface Service
{
    String ROLE = Service.class.getName();

    WSDL getWSDL() throws WSDLException;

    Handler getServiceHandler();
    
    /**
     * The fault handler which handles exception which occurr during the
     * invocation of the Service Handler.
     * @return
     */
    FaultHandler getFaultHandler();
    
    /**
     * Return the service style.  Can be document, rpc, wrapped, or message.
     * @return
     */
    String getStyle();

    /**
     * Return the Use.  Messages can be encoded or literal.
     * @return
     */
    String getUse();

    /**
     * The name of the service. This must be URI encodable.
     */
    String getName();

    /**
     * The namespace of the service.
     * 
     * @return
     */
    String getDefaultNamespace();

    void setProperty(String name, Object value);

    Object getProperty(String name);
    
    String getSoapVersion();
}
