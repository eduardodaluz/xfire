package org.codehaus.xfire.jaxws.gen;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.jsr181.Jsr181WebAnnotations;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.gen.GenerationContext;
import org.codehaus.xfire.gen.GeneratorPlugin;
import org.codehaus.xfire.gen.jsr181.AbstractPlugin;
import org.codehaus.xfire.gen.jsr181.ServiceInterfaceGenerator;
import org.codehaus.xfire.gen.jsr181.ServiceStubGenerator;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapBinding;
import org.codehaus.xfire.soap.SoapTransport;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.transport.local.LocalTransport;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JMods;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class ServiceGenerator
    extends AbstractPlugin
    implements GeneratorPlugin
{
    public void generate(GenerationContext context)
        throws Exception
    {
        Service service = context.getService();
        
        String name = service.getSimpleName();
        String ns = service.getTargetNamespace();
        
        if (service.getEndpoints().size() == 0) return;
        
        // hack to get local support
        SoapBinding localBind = new SoapBinding(new QName(ns, name + "LocalBinding"), service);
        localBind.setTransportURI(LocalTransport.BINDING_ID);
        service.addBinding(localBind);
        service.addEndpoint(new QName(ns, name + "LocalPort"), localBind, "xfire.local://" + name);
        
        JCodeModel model = context.getCodeModel();

        String portName = context.getDestinationPackage() + "." + service.getName().getLocalPart() + "Service";
        portName = getUniqueName(model, portName);
        
        JDefinedClass servCls = model._class(portName);
        servCls._extends(javax.xml.ws.Service.class);
        
        JDefinedClass serviceImpl = (JDefinedClass) context.getProperty(ServiceStubGenerator.SERVICE_STUB);
        JDefinedClass serviceIntf = (JDefinedClass) context.getProperty(ServiceInterfaceGenerator.SERVICE_INTERFACE);
        
        JFieldVar intfClass = servCls.field(JMod.STATIC + JMod.PUBLIC,
                                            Class.class,
                                            "SERVICE_INTERFACE",
                                            JExpr.dotclass(serviceIntf));
        
        JFieldVar serviceClass = servCls.field(JMod.STATIC + JMod.PUBLIC, 
                                               Class.class, 
                                               "SERVICE_CLASS",
                                               JExpr.dotclass(serviceImpl));
                                 
        String wsdlUrl = new File(context.getWsdlLocation()).toURL().toExternalForm();
        JAnnotationUse clientAnn = servCls.annotate(WebServiceClient.class);
        clientAnn.param("targetNamespace", ns);
        clientAnn.param("name", name);
        clientAnn.param("wsdlLocation", wsdlUrl);
        
        JType qnameType = model._ref(QName.class);
        
        /**
         * Constructor
         */
        JMethod constructor = servCls.constructor(JMod.PUBLIC);
        constructor._throws(MalformedURLException.class);
        
        JType urlType = model._ref(URL.class);
        
        JInvocation newURL = JExpr._new(urlType).arg(wsdlUrl);
        JInvocation newSN = JExpr._new(qnameType).arg(ns).arg(name);
        
        JInvocation superService = JExpr.invoke("super").arg(newURL).arg(newSN);
        constructor.body().add(superService);
        
        for (Iterator itr = service.getEndpoints().iterator(); itr.hasNext();)
        {
            Endpoint endpoint = (Endpoint) itr.next();

            JInvocation newQN = JExpr._new(qnameType);
            newQN.arg(endpoint.getName().getNamespaceURI());
            newQN.arg(endpoint.getName().getLocalPart());
            
            JInvocation bindingQN = JExpr._new(qnameType);
            bindingQN.arg(endpoint.getBinding().getName().getNamespaceURI());
            bindingQN.arg(endpoint.getBinding().getName().getLocalPart());
 

//          Add a getFooEndpointMethod
            JMethod getFooEndpoint = servCls.method(JMod.PUBLIC, serviceIntf, "get" + endpoint.getName().getLocalPart());
            JBlock geBody = getFooEndpoint.body();

            geBody._return(JExpr.cast(serviceIntf, JExpr.direct("this").invoke("getPort").arg(newQN).arg(intfClass)));
            
            JAnnotationUse weAnn = getFooEndpoint.annotate(WebEndpoint.class);
            weAnn.param("name", endpoint.getName().getLocalPart());
        }
    }

}
