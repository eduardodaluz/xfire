package org.codehaus.xfire.annotations;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.annotations.soap.SOAPBindingAnnotation;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.codehaus.xfire.util.NamespaceHelper;
import org.codehaus.xfire.wsdl.ResourceWSDL;

/**
 * Annotations-bases implementation of the {@link ServiceFactory} interface.
 *
 * @author Arjen Poutsma
 */
public class AnnotationServiceFactory
        extends ObjectServiceFactory
        implements ServiceFactory
{
    private Log log = LogFactory.getLog(AnnotationServiceFactory.class);
    
    private WebAnnotations webAnnotations;

    public static final String ALLOW_INTERFACE = "annotations.allow.interface";
    
    /**
     * Creates an AnnotationServiceFactory which uses the most appropriate
     * annotations implementation - commons-attributes on Java 1.4 and
     * Java 5 attributes on Java 5 JVMs.
     * <p>
     * The {@link TransportManager} is retrieved from the {@link XFireFactory}.
     */
    public AnnotationServiceFactory()
    {
        this(XFireFactory.newInstance().getXFire().getTransportManager());
    }
    

    /**
     * Creates an AnnotationServiceFactory which uses the most appropriate
     * annotations implementation - commons-attributes on Java 1.4 and
     * Java 5 attributes on Java 5 JVMs.
     */
    public AnnotationServiceFactory(final TransportManager transportManager)
    {
        super(transportManager, new AegisBindingProvider());
        this.webAnnotations = getAnnotations();
        
        AnnotationServiceConfiguration annotationConfig = new AnnotationServiceConfiguration();
        annotationConfig.setWebAnnotations(webAnnotations);
        annotationConfig.setServiceFactory(this);
        getServiceConfigurations().add(0, annotationConfig);
    }

    public AnnotationServiceFactory(WebAnnotations webAnnotations,
                                    final TransportManager transportManager)
    {
        super(transportManager, new AegisBindingProvider());
        this.webAnnotations = webAnnotations;

        AnnotationServiceConfiguration annotationConfig = new AnnotationServiceConfiguration();
        annotationConfig.setWebAnnotations(webAnnotations);
        annotationConfig.setServiceFactory(this);
        getServiceConfigurations().add(0, annotationConfig);
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

        AnnotationServiceConfiguration annotationConfig = new AnnotationServiceConfiguration();
        annotationConfig.setWebAnnotations(webAnnotations);
        annotationConfig.setServiceFactory(this);
        getServiceConfigurations().add(0, annotationConfig);
    }

    protected WebAnnotations getAnnotations()
    {
        if (!isJDK5andAbove())
            return loadCommonsAttributesAnnotations();
        
        try
        {
            WebAnnotations wa = (WebAnnotations)
                ClassLoaderUtils.loadClass("org.codehaus.xfire.annotations.jsr181.Jsr181WebAnnotations", 
                                           getClass()).newInstance();
            
            return wa;
        }
        catch (Exception e)
        {
            return loadCommonsAttributesAnnotations();
        }
    }


    private WebAnnotations loadCommonsAttributesAnnotations()
    {
        try
        {
            WebAnnotations wa = (WebAnnotations)
                ClassLoaderUtils.loadClass("org.codehaus.xfire.annotations.commons.CommonsWebAttributes", 
                                           getClass()).newInstance();
            
            return wa;
        }
        catch (Exception e1)
        {
            throw new XFireRuntimeException("No WebAnnotation implementation was found!", e1);
        }
    }
    
    boolean isJDK5andAbove()
    {
      String v = System.getProperty("java.class.version","44.0");
      return ("49.0".compareTo(v) <= 0);
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
    public Service create(final Class clazz, String name, String namespace, Map properties)
    {
        String style = null;
        String use = null;
        
        if (properties == null) properties = new HashMap();

        if (webAnnotations.hasWebServiceAnnotation(clazz))
        {
            WebServiceAnnotation webServiceAnnotation = webAnnotations.getWebServiceAnnotation(clazz);
         
            assertValidImplementationClass(clazz, webAnnotations, properties);
            
            name = createServiceName(clazz, webServiceAnnotation, name);
         
            /* Attempt to load the endpoint interface if there is one. If there is an endpoint
             * interface the attribute WebService.serviceName is the only valid one for the
             * implementing bean class.
             */
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

                    namespace = createServiceNamespace(endpointInterface, endpointWSAnnotation, namespace);
                    portType = createPortType(name, endpointWSAnnotation);
                }
                catch (ClassNotFoundException e)
                {
                    throw new AnnotationException("Couldn't find endpoint interface " +
                                                  webServiceAnnotation.getEndpointInterface(), e);
                }
                
            }
            else
            {
                namespace = createServiceNamespace(endpointInterface, webServiceAnnotation, namespace);
                portType = createPortType(name, webServiceAnnotation);
            }
            
            if (webAnnotations.hasSOAPBindingAnnotation(endpointInterface))
            {
                SOAPBindingAnnotation soapBindingAnnotation = 
                    webAnnotations.getSOAPBindingAnnotation(endpointInterface);
                
                style = soapBindingAnnotation.getStyleString();
                use =  soapBindingAnnotation.getUseString();
            }

            // Allow the user to override informations given in the annotations
            if (!properties.containsKey(PORT_TYPE))
            {
            	properties.put(PORT_TYPE, new QName(namespace, portType));
            }
            if (!properties.containsKey(STYLE))
            {
	            properties.put(STYLE, style);
            }
            if (!properties.containsKey(USE))
            {
	            properties.put(USE, use);
            }
            
            
            Map props = webAnnotations.getServiceProperties(clazz);
            if(props!= null){
            	properties.putAll(props);	
            }
            
            if (webAnnotations.hasHandlerChainAnnotation(clazz))
            {
                log.error("@HandlerChain attributes are not supported yet!");
            }
            
            Service service = super.create(endpointInterface, name, namespace, properties);

            String wsdl = webServiceAnnotation.getWsdlLocation();
            if (wsdl != null && wsdl.length() > 0)
            {
                try
                {
                    service.setWSDLWriter(new ResourceWSDL(wsdl));
                }
                catch (IOException e)
                {
                    throw new AnnotationException("Couldn't load wsdl from " + wsdl, e);
                }
            }
            
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

    private void assertValidImplementationClass(Class clazz, WebAnnotations webAnnotations2, Map properties)
    {
        if (Modifier.isAbstract(clazz.getModifiers()) && !Boolean.TRUE.equals(properties.get(ALLOW_INTERFACE)))
        {
            throw new AnnotationException("Service class cannot be abstract: " + clazz.getName());
        }
        
        if (Modifier.isFinal(clazz.getModifiers()))
        {
            throw new AnnotationException("Service class cannot be final: " + clazz.getName());
        }
        
        if (/*clazz.isMemberClass() ||*/ !Modifier.isPublic(clazz.getModifiers()))
        {
            throw new AnnotationException("Service class must be an outter public class: " + clazz.getName());
        }
        
        WebServiceAnnotation wsAnn = webAnnotations2.getWebServiceAnnotation(clazz);
        if (wsAnn.getEndpointInterface().length() > 0)
        {
            Method[] methods = clazz.getMethods();
            for (int i = 0; i < methods.length; i++)
            {
                if (methods[i].getDeclaringClass().equals(clazz) 
                        && webAnnotations2.hasWebMethodAnnotation(methods[i]))
                {
                    throw new AnnotationException("@WebMethod attributes are only allowed on the endpointInterface! " +
                            "Offending class: " + clazz.getName());
                }
            }
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

    protected String createServiceNamespace(Class clazz, WebServiceAnnotation webServiceAnnotation, String current)
    {
        String ns = current;
        if (ns == null && webServiceAnnotation.getTargetNamespace().length() > 0)
        {
            ns = webServiceAnnotation.getTargetNamespace();
        }
        
        if (ns == null)
        {
            ns = NamespaceHelper.makeNamespaceFromClassName(clazz.getName(), "http");
        }
        return ns;
    }

    protected String createServiceName(Class clazz, WebServiceAnnotation webServiceAnnotation, String current)
    {
        String name = current;
        if (name == null && webServiceAnnotation.getServiceName().length() > 0)
        {
            name = webServiceAnnotation.getServiceName();
        }

        if (name == null)
        {
            name = makeServiceNameFromClassName(clazz);
        }
        return name;
    }

    protected String createPortType(String name, WebServiceAnnotation webServiceAnnotation)
    {
        String portType = null;
        if (webServiceAnnotation.getName().length() > 0)
        {
            portType = webServiceAnnotation.getName();
        }
        else
        {
            portType = name + "PortType";
        }

        return portType;
    }


    public Service create(Class clazz, QName name, URL wsdlUrl, Map properties)
    {
        if (properties == null)
            properties = new HashMap();
        
        properties.put(ALLOW_INTERFACE, Boolean.TRUE);
        
        return super.create(clazz, name, wsdlUrl, properties);
    }
}
