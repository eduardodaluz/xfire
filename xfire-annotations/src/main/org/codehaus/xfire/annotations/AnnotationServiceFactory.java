package org.codehaus.xfire.annotations;

import java.lang.reflect.Method;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.object.ObjectService;
import org.codehaus.xfire.service.object.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.type.TypeMappingRegistry;
import org.codehaus.xfire.util.NamespaceHelper;

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

    public AnnotationServiceFactory(WebAnnotations webAnnotations, TransportManager transportManager,
                                    TypeMappingRegistry registry)
    {
        super(transportManager, registry);
        this.webAnnotations = webAnnotations;
    }

    public Service create(Class clazz, SoapVersion version, String style, String use)
    {
        WebServiceAnnotation webServiceAnnotation = webAnnotations.getWebServiceAnnotation(clazz);
        if (webServiceAnnotation != null)
        {
            String serviceName = createServiceName(clazz, webServiceAnnotation);
         
            /* Attempt to load the endpoint interface if there is one. If there is an endpoint
             * interface the attribute WebService.serviceName is the only valid one for the 
             * implementing bean class.
             */
            String tns = null;
            Class endpointInterface = clazz;
            if (webServiceAnnotation.getEndpointInterface() != null)
            {
                try
                {
                    endpointInterface = loadClass(webServiceAnnotation.getEndpointInterface());
                    WebServiceAnnotation endpointWSAnnotation = 
                        webAnnotations.getWebServiceAnnotation(endpointInterface);
                    
                    tns = createServiceNamespace(endpointInterface, endpointWSAnnotation);
                }
                catch (ClassNotFoundException e)
                {
                    throw new XFireRuntimeException("Couldn't find endpoint interface " + endpointInterface, e);
                }
            }
            else
            {
                tns = createServiceNamespace(endpointInterface, webServiceAnnotation);
            }
            
            Service service = create(endpointInterface, serviceName, tns, version, style, use, null);
            
            if (clazz != endpointInterface)
            {
                service.setProperty(ObjectService.SERVICE_IMPL_CLASS, clazz);
            }
            
            return service;
        }
        else
        {
            throw new XFireRuntimeException("Class " + clazz.getName() + 
                                            " does not have a WebService annotation");
        }
    }

    /**
     * Attempt to load a class first from this class's ClassLoader, then from the context
     * classloader.
     * 
     * @param endpointInterface
     * @return
     * @throws ClassNotFoundException 
     */
    protected Class loadClass(String endpointInterface) throws ClassNotFoundException
    {
        try
        {
            return getClass().getClassLoader().loadClass(endpointInterface);
        }
        catch (ClassNotFoundException e)
        {
            try
            {
                return Thread.currentThread().getContextClassLoader().loadClass(endpointInterface);
            }
            catch (ClassNotFoundException e1)
            {
                throw e;
            }
        }
    }

    protected String createServiceNamespace(Class clazz, WebServiceAnnotation webServiceAnnotation)
    {
        String ns = null;
        if (webServiceAnnotation.getTargetNamespace() != null)
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
        if (webServiceAnnotation.getName() != null)
        {
            name = webServiceAnnotation.getName();
        }
        else
        {
            name = makeServiceNameFromClassName(clazz);
        }
        return name;
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
        if (!method.getDeclaringClass().isInterface())
            return webAnnotations.hasWebMethodAnnotation(method);
        
        return true;
    }
}
