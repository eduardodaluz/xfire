package org.codehaus.xfire.service.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.Soap11FaultHandler;
import org.codehaus.xfire.fault.Soap12FaultHandler;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.ServiceEndpointAdapter;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.bridge.ObjectServiceHandler;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.soap.SoapHandler;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.util.NamespaceHelper;
import org.codehaus.xfire.util.ServiceUtils;
import org.codehaus.xfire.wsdl.ResourceWSDL;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;

/**
 * Java objects-specific implementation of the {@link ServiceFactory} interface.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class ObjectServiceFactory
        implements ServiceFactory
{
    private BindingProvider provider;
    private TransportManager transportManager;

    /**
     * Initializes a new instance of the <code>ObjectServiceFactory</code>.
     */
    public ObjectServiceFactory()
    {
    }

    /**
     * Initializes a new instance of the <code>ObjectServiceFactory</code> with the given transport manager and type
     * mapping registry.
     *
     * @param transportManager the transport manager
     * @param provider         the binding provider
     */
    public ObjectServiceFactory(TransportManager transportManager, BindingProvider provider)
    {
        this.provider = provider;
        this.transportManager = transportManager;
    }

    private BindingProvider getBindingProvider()
    {
        if (provider == null)
        {
            try
            {
                provider = (BindingProvider) getClass().getClassLoader()
                        .loadClass("org.codehaus.xfire.aegis.AegisBindingProvider").newInstance();
            }
            catch (Exception e)
            {
                throw new XFireRuntimeException("Couldn't find a binding provider!", e);
            }
        }

        return provider;
    }


    /**
     * @param wsdlUrl
     * @return
     */
    public Service create(Class clazz, URL wsdlUrl)
            throws Exception
    {
        WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
        reader.readWSDL(wsdlUrl.toString());

        QName name = ServiceUtils.makeQualifiedNameFromClass(clazz);
        ServiceInfo serviceInfo = new ServiceInfo(name, clazz);
        ServiceEndpoint endpoint = new ServiceEndpoint(serviceInfo);
        endpoint.setBindingProvider(getBindingProvider());

        endpoint.setFaultHandler(new Soap11FaultHandler());
        endpoint.setWSDL(new ResourceWSDL(wsdlUrl));

        SoapHandler handler = new SoapHandler(new ObjectServiceHandler());
        endpoint.setServiceHandler(handler);
        endpoint.setInvoker(new ObjectInvoker());

        return new ServiceEndpointAdapter(endpoint);
    }

    /**
     * Creates a service from the specified class. The returned service will have a name based on the class name, a
     * namespace based on the class package, soap version 1.1, wrapped style, and literal use.
     *
     * @param clazz The service class used to populate the operations and parameters.
     * @return The service.
     */
    public Service create(Class clazz)
    {
        return create(clazz, null, null, null);
    }

    /**
     * Creates a service from the specified class, soap version, style and use. The returned service will have a name
     * based on the class name, and a namespace based on the class package.
     * <p/>
     * Some parameters can be <code>null</code>, and will be replaced with sensible defaults if so. See the specific
     * parameters for more info.
     *
     * @param clazz   The service class used to populate the operations and parameters.
     * @param version The soap version. If <code>null</code>, {@link Soap11} will be used.
     * @param style   The service style. If <code>null</code>, {@link SoapConstants#STYLE_WRAPPED} will be used.
     * @param use     The service use. If <code>null</code>, {@link SoapConstants#USE_LITERAL} will be used.
     * @return The service.
     */
    public Service create(Class clazz, SoapVersion version, String style, String use)
    {
        return create(clazz, null, null, version, style, use, null);
    }

    protected String makeServiceNameFromClassName(Class clazz)
    {
        String name = clazz.getName();
        int last = name.lastIndexOf(".");
        if (last != -1)
        {
            name = name.substring(last + 1);
        }
        return name;
    }

    /**
     * Creates a service from the specified class and parameters.
     * <p/>
     * Most parameters can be <code>null</code>, and will be replaced with sensible defaults if so. See the specific
     * parameters for more info.
     *
     * @param clazz            The service class used to populate the operations and parameters.
     * @param name             The name of the service. If <code>null</code>, a name will be generated from the class
     *                         name.
     * @param namespace        The default namespace of the service. If <code>null</code>, a namespace will be generated
     *                         from the class package.
     * @param version          The soap version. If <code>null</code>, {@link org.codehaus.xfire.soap.Soap11} will be
     *                         used.
     * @param style            The service style. If <code>null</code>, {@link org.codehaus.xfire.soap.SoapConstants#STYLE_WRAPPED}
     *                         will be used.
     * @param use              The service use. If <code>null</code>, {@link org.codehaus.xfire.soap.SoapConstants#USE_LITERAL}
     *                         will be used.
     * @param encodingStyleURI The encoding style to use.
     * @return The service.
     */
    public Service create(Class clazz,
                          String name,
                          String namespace,
                          SoapVersion version,
                          String style,
                          String use,
                          String encodingStyleURI)
    {
        String theName = (name != null) ? name : makeServiceNameFromClassName(clazz);
        String theNamespace = (namespace != null) ? namespace : NamespaceHelper.makeNamespaceFromClassName(
                clazz.getName(), "http");
        QName qName = new QName(theNamespace, theName);
        SoapVersion theVersion = (version != null) ? version : Soap11.getInstance();
        String theStyle = (style != null) ? style : SoapConstants.STYLE_WRAPPED;
        String theUse = (use != null) ? use : SoapConstants.USE_LITERAL;

        ServiceInfo serviceInfo = new ServiceInfo(qName, clazz);
        SOAPBinding binding = null;
        QName bindingQName = new QName(theName + "Binding");
        if (theStyle.equals(SoapConstants.STYLE_DOCUMENT) && theUse.equals(SoapConstants.USE_LITERAL))
        {
            binding = new DocumentBinding(bindingQName, theVersion);
        }
        else if (theStyle.equals(SoapConstants.STYLE_WRAPPED) && theUse.equals(SoapConstants.USE_LITERAL))
        {
            binding = new WrappedBinding(bindingQName, theVersion);
        }
        else if (theStyle.equals(SoapConstants.STYLE_RPC) && theUse.equals(SoapConstants.USE_ENCODED))
        {
            binding = new RPCEncodedBinding(bindingQName, theVersion);
        }
        else
        {
            throw new IllegalArgumentException("Illegal style/use combination [" + style + "/" + use + "]");
        }
        ServiceEndpoint endpoint = new ServiceEndpoint(serviceInfo, binding);
        ServiceEndpointAdapter adapter = new ServiceEndpointAdapter(endpoint);
        try
        {
            endpoint.setBindingProvider(getBindingProvider());
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException("Couldn't load provider.", e);
        }


        if (encodingStyleURI != null)
            endpoint.setProperty("type.encodingUri", encodingStyleURI);

        if (version instanceof Soap11)
        {
            endpoint.setFaultHandler(new Soap11FaultHandler());
        }
        else
        {
            endpoint.setFaultHandler(new Soap12FaultHandler());
        }

        if (transportManager != null)
        {
            endpoint.setWSDLBuilder(new WSDLBuilder(transportManager));
        }

        SoapHandler handler = new SoapHandler(endpoint.getBindingProvider().createEndpointHandler());
        endpoint.setServiceHandler(handler);
        endpoint.setInvoker(new ObjectInvoker());

        initializeOperations(endpoint);

        endpoint.getBindingProvider().initialize(adapter);

        return adapter;
    }

    protected void initializeOperations(ServiceEndpoint endpoint)
    {
        final Method[] methods = endpoint.getService().getServiceClass().getDeclaredMethods();

        // TODO: go through superclasses, stopping at Object.class

        for (int i = 0; i < methods.length; i++)
        {
            final Method method = methods[i];

            if (isValidMethod(method))
            {
                addOperation(endpoint, method);
            }
        }
    }

    protected boolean isValidMethod(final Method method)
    {
        final int modifiers = method.getModifiers();

        return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers);
    }

    protected void addOperation(ServiceEndpoint endpoint, final Method method)
    {
        ServiceEndpointAdapter adapter = new ServiceEndpointAdapter(endpoint);
        ServiceInfo service = endpoint.getService();
        final OperationInfo op = service.addOperation(method.getName(), method);

        final Class[] paramClasses = method.getParameterTypes();

        final boolean isDoc = adapter.getStyle().equals(SoapConstants.STYLE_DOCUMENT);

        MessageInfo inMsg = op.createMessage(new QName(op.getName() + "Request"));
        op.setInputMessage(inMsg);

        for (int j = 0; j < paramClasses.length; j++)
        {
            final QName q = getInParameterName(endpoint, method, j, isDoc);
            inMsg.addMessagePart(q, paramClasses[j]);
        }

        MessageInfo outMsg = op.createMessage(new QName(op.getName() + "Response"));
        op.setOutputMessage(outMsg);

        final Class returnType = method.getReturnType();
        if (!returnType.isAssignableFrom(void.class))
        {
            final QName q = getOutParameterName(endpoint, method, isDoc);
            outMsg.addMessagePart(q, method.getReturnType());
        }

        op.setOneWay(isAsync(method));
    }

    protected boolean isAsync(final Method method)
    {
        return false;
    }

    protected QName getInParameterName(ServiceEndpoint endpoint,
                                       final Method method,
                                       final int paramNumber,
                                       final boolean doc)
    {
        String paramName = "";
        if (doc)
            paramName = method.getName();

        return new QName(endpoint.getService().getName().getNamespaceURI(), paramName + "in" + paramNumber);
    }

    protected QName getOutParameterName(ServiceEndpoint endpoint, final Method method, final boolean doc)
    {
        String outName = "";
        if (doc)
            outName = method.getName();

        return new QName(endpoint.getService().getName().getNamespaceURI(), outName + "out");
    }
}
