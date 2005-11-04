package org.codehaus.xfire.annotations;

import java.lang.reflect.Method;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.annotations.soap.SOAPBindingAnnotation;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.util.NamespaceHelper;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilderInfo;

/**
 * Annotations-bases implementation of the {@link ServiceFactory} interface.
 *
 * @author Arjen Poutsma
 */
public class AnnotationServiceFactory
        extends ObjectServiceFactory
        implements ServiceFactory
{
    private WebAnnotations webAnnotations;

    public AnnotationServiceFactory(WebAnnotations webAnnotations,
                                    final TransportManager transportManager)
    {
        super(transportManager, new AegisBindingProvider());
        this.webAnnotations = webAnnotations;
    }
    
    /**
     * Initializes a new instance of the <code>AnnotationServiceFactory</code> with the given annotations facade,
     * transport manager and type mapping registry.
     *
     * @param webAnnotations   the annotations facade
     * @param transportManager the transport manager
     * @param provider         the registry
     */
    public AnnotationServiceFactory(WebAnnotations webAnnotations,
                                    final TransportManager transportManager,
                                    final BindingProvider provider)
    {
        super(transportManager, provider);
        this.webAnnotations = webAnnotations;
    }

    /**
     * Creates a service from the specified class. If the class has a
     * {@link SOAPBindingAnnotation}, it will be used to define the style and
     * use of the service. If the class has a {@link WebServiceAnnotation}, it will be used to
     * define the name, service name, target namespace. If the annotation
     * defines an endpoint interface, all methods of that interface are exposed
     * as operations. If no endpoint interface is defined, all methods that have
     * the {@link WebMethodAnnotation} are exposed.
     * 
     * @param clazz
     *            The service class used to populate the operations and
     *            parameters.
     * @return The service.
     */
    public Service create(final Class clazz, final Map properties)
    {
        String style = null;
        String use = null;
        
        if (webAnnotations.hasSOAPBindingAnnotation(clazz))
        {
            SOAPBindingAnnotation soapBindingAnnotation = webAnnotations.getSOAPBindingAnnotation(clazz);
            
            style = soapBindingAnnotation.getStyleString();
            use =  soapBindingAnnotation.getUseString();
        }

        if (webAnnotations.hasWebServiceAnnotation(clazz))
        {
            WebServiceAnnotation webServiceAnnotation = webAnnotations.getWebServiceAnnotation(clazz);

            String serviceName = createServiceName(clazz, webServiceAnnotation);
         
            /* Attempt to load the endpoint interface if there is one. If there is an endpoint
             * interface the attribute WebService.serviceName is the only valid one for the
             * implementing bean class.
             */
            String tns = null;
            String portType = null;
            Class endpointInterface = clazz;
            if (webServiceAnnotation.getEndpointInterface() != null &&
                    webServiceAnnotation.getEndpointInterface().length() != 0)
            {
                try
                {
                    endpointInterface = loadClass(webServiceAnnotation.getEndpointInterface());
                    if (!webAnnotations.hasWebServiceAnnotation(endpointInterface))
                    {
                        throw new AnnotationException("Endpoint interface " + endpointInterface.getName() +
                                                      " does not have a WebService annotation");
                    }
                    WebServiceAnnotation endpointWSAnnotation =
                            webAnnotations.getWebServiceAnnotation(endpointInterface);

                    tns = createServiceNamespace(endpointInterface, endpointWSAnnotation);
                    portType = createPortType(serviceName, endpointWSAnnotation);
                }
                catch (ClassNotFoundException e)
                {
                    throw new AnnotationException("Couldn't find endpoint interface " +
                                                  webServiceAnnotation.getEndpointInterface(), e);
                }
            }
            else
            {
                tns = createServiceNamespace(endpointInterface, webServiceAnnotation);
                portType = createPortType(serviceName, webServiceAnnotation);
            }

            Service service = create(endpointInterface, serviceName, tns, null, style, use, properties);

            // Fill in WSDL Builder metadata from annotations.
            WSDLBuilderInfo info = new WSDLBuilderInfo(service);
            info.setTargetNamespace(tns);
            info.setServiceName(serviceName);
            info.setPortType(portType);
            service.setProperty(WSDLBuilderInfo.KEY, info);

            if (clazz != endpointInterface)
            {
                service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, clazz);
            }

            return service;
        }
        else
        {
            throw new AnnotationException("Class " + clazz.getName() + " does not have a WebService annotation");
        }
    }

    /**
     * Attempt to load a class first from this class's ClassLoader, then from the context classloader.
     *
     * @param endpointInterface
     * @return
     * @throws ClassNotFoundException
     */
    protected Class loadClass(String endpointInterface)
            throws ClassNotFoundException
    {
        return ClassLoaderUtils.loadClass(endpointInterface, getClass());
    }

    protected String createServiceNamespace(Class clazz, WebServiceAnnotation webServiceAnnotation)
    {
        String ns = null;
        if (webServiceAnnotation.getTargetNamespace().length() > 0)
        {
            ns = webServiceAnnotation.getTargetNamespace();
        }
        else
        {
            ns = NamespaceHelper.makeNamespaceFromClassName(clazz.getName(), "http");
        }
        return ns;
    }

    protected String createServiceName(Class clazz, WebServiceAnnotation webServiceAnnotation)
    {
        String name = null;
        if (webServiceAnnotation.getServiceName().length() > 0)
        {
            name = webServiceAnnotation.getServiceName();
        }
        else
        {
            name = makeServiceNameFromClassName(clazz);
        }
        return name;
    }

    protected String createPortType(String serviceName, WebServiceAnnotation webServiceAnnotation)
    {
        String portType = null;
        if (webServiceAnnotation.getName().length() > 0)
        {
            portType = webServiceAnnotation.getName();
        }
        else
        {
            portType = serviceName + "PortType";
        }

        return portType;
    }

    protected String getAction(OperationInfo op)
    {
        if (webAnnotations.hasWebMethodAnnotation(op.getMethod()))
        {
            WebMethodAnnotation wma = webAnnotations.getWebMethodAnnotation(op.getMethod());
            if (wma.getAction().length() > 0)
                return wma.getAction();
        }
        
        return super.getAction(op);
    }

    /**
     * Returns <code>true</code> if the specified method is valid for a SOAP operation.
     *
     * @param method the method.
     * @return <code>true</code> if valid; <code>false</code> otherwise.
     */
    protected boolean isValidMethod(Method method)
    {
        if (!super.isValidMethod(method))
            return false;
        
        // All methods on endpoint interfaces are valid WebMethods.
        if (method.getDeclaringClass().isInterface())
            return true;

        return webAnnotations.hasWebMethodAnnotation(method);
    }

    protected boolean isHeader(Method method, int paramNumber)
    {
        if (paramNumber != -1)
        {
            if (webAnnotations.hasWebParamAnnotation(method, paramNumber))
            {
                final WebParamAnnotation webParamAnnotation = 
                    webAnnotations.getWebParamAnnotation(method, paramNumber);
                
                return webParamAnnotation.isHeader();
            }
        }

        return super.isHeader(method, paramNumber);
    }

    protected QName getInParameterName(Service endpoint, OperationInfo op, Method method, int paramNumber, boolean doc)
    {
        if (webAnnotations.hasWebParamAnnotation(method, paramNumber))
        {
            final WebParamAnnotation webParamAnnotation = webAnnotations.getWebParamAnnotation(method, paramNumber);

            String name = webParamAnnotation.getName();
            String ns = webParamAnnotation.getTargetNamespace();

            if (ns == null || ns.length() == 0) 
            {
                ns = endpoint.getServiceInfo().getName().getNamespaceURI();
            }

            return new QName(ns, name);
        }
        else
        {
            return super.getInParameterName(endpoint, op, method, paramNumber, doc);
        }
    }

    protected QName getOutParameterName(Service endpoint, OperationInfo op, Method method, boolean doc)
    {
        if (webAnnotations.hasWebResultAnnotation(method))
        {
            final WebResultAnnotation webResultAnnotation = webAnnotations.getWebResultAnnotation(method);

            String name = webResultAnnotation.getName();
            String ns = webResultAnnotation.getTargetNamespace();

            if (ns == null || ns.length() == 0)
            {
                ns = endpoint.getServiceInfo().getName().getNamespaceURI();
            }

            return new QName(ns, name);
        }
        else
        {
            return super.getOutParameterName(endpoint, op, method, doc);
        }
    }

    protected boolean isAsync(Method method)
    {
        return webAnnotations.hasOnewayAnnotation(method);
    }

    protected String getMEP(Method method)
    {
        if (webAnnotations.hasOnewayAnnotation(method))
        {
            return SoapConstants.MEP_IN;
        }
        
        return super.getMEP(method);
    }

}
