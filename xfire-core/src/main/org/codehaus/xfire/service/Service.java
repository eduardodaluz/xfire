package org.codehaus.xfire.service;

import java.util.Collection;
import javax.wsdl.WSDLException;

import org.codehaus.xfire.fault.FaultHandler;
import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.Invoker;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.wsdl.WSDLWriter;

/**
 * A service descriptor. This class must be thread safe.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface Service
{
    String ROLE = Service.class.getName();
    public static final String ALLOWED_METHODS = "allowedMethods";
    public final static int SCOPE_APPLICATION = 1;
    public final static int SCOPE_REQUEST = 3;
    public final static int SCOPE_SESSION = 2;
    public static final String SERVICE_IMPL_CLASS = "xfire.serviceImplClass";

    WSDLWriter getWSDLWriter()
            throws WSDLException;

    Handler getServiceHandler();

    HandlerPipeline getRequestPipeline();

    HandlerPipeline getResponsePipeline();

    FaultHandlerPipeline getFaultPipeline();

    /**
     * The fault handler which handles exception which occur during processing.
     *
     * @return
     */
    FaultHandler getFaultHandler();

    /**
     * Return the service style.  Can be document, rpc, wrapped, or message.
     *
     * @return
     */
    String getStyle();

    /**
     * Return the Use.  Messages can be encoded or literal.
     *
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

    SoapVersion getSoapVersion();

    BindingProvider getBindingProvider();

    Invoker getInvoker();

    /**
     * Get an operation.
     *
     * @param name The name of the operation.
     * @return
     */
    OperationInfo getOperation(String name);

    void removeOperation(String string);

    void addOperation(OperationInfo info);

    /**
     * Get all the operations for this service.
     *
     * @return
     */
    Collection getOperations();

    int getScope();

    /**
     * The class which the operations map to.
     *
     * @return
     */
    Class getServiceClass();

    void setInvoker(Invoker invoker);

}
