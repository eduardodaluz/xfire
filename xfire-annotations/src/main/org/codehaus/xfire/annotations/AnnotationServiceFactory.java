package org.codehaus.xfire.annotations;

import java.lang.reflect.Method;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
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
            String name = null;
            if (webServiceAnnotation.getName() != null)
            {
                name = webServiceAnnotation.getName();
            }
            else
            {
                name = makeServiceNameFromClassName(clazz);
            }

            String ns = null;
            if (webServiceAnnotation.getTargetNamespace() != null)
            {
                ns = webServiceAnnotation.getTargetNamespace();
            }
            else
            {
                ns = NamespaceHelper.makeNamespaceFromClassName(clazz.getName(), "http");
            }
            return create(clazz, name, ns, version, style, use, null);
        }
        else
        {
            throw new XFireRuntimeException("Class " + clazz.getName() + " does not have a WebService annotation");
        }
    }

    /**
     * Returns <code>true</code> if the specified method is valid for a SOAP operation.
     *
     * @param method the method.
     * @return <code>true</code> if valid; <code>false</code> otherwise.
     */
    protected boolean isValidMethod(Method method)
    {
        return super.isValidMethod(method) && webAnnotations.hasWebMethodAnnotation(method);
    }


}
