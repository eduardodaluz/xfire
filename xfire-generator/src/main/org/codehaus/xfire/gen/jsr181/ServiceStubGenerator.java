package org.codehaus.xfire.gen.jsr181;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.namespace.QName;

import org.codehaus.xfire.gen.GenerationContext;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

public class ServiceStubGenerator
    extends AbstractServiceGenerator
{
    public static final String SERVICE_STUB = "service.stub";
    
    @Override
    public void generate(GenerationContext context, Service service)
        throws Exception
    {
        if (service.getEndpoints().size() == 0) return;
        
        super.generate(context, service);
    }

    @Override
    protected String getClassName(GenerationContext context, Service service)
    {
        QName name = service.getName();
        String pckg = context.getDestinationPackage();
        String clsName = javify(name.getLocalPart());

        return getUniqueName(context.getCodeModel(), pckg + "." + clsName + "Impl");
    }

    @Override
    protected void annotate(GenerationContext context, Service service, JDefinedClass jc)
    {
        super.annotate(context, service, jc);
        
        JAnnotationUse ann = jc.annotate(WebService.class);
        
        ann.param("serviceName", service.getSimpleName());
        ann.param("targetNamespace", service.getTargetNamespace());
        
        JClass intf = (JClass) service.getProperty(ServiceInterfaceGenerator.SERVICE_INTERFACE);
        ann.param("endpointInterface", intf.fullName());
        
        jc._implements(intf);
        
        ann = jc.annotate(SOAPBinding.class);
        ann.param("style", Style.DOCUMENT);
        ann.param("use", Use.LITERAL);
        
        if (service.getServiceInfo().isWrapped())
        {
            ann.param("parameterStyle", ParameterStyle.WRAPPED);
        }
        else
        {
           ann.param("parameterStyle", ParameterStyle.BARE);
        }
        
        service.setProperty(SERVICE_STUB, jc);
    }
    
    @Override
    protected void annotate(GenerationContext context, OperationInfo op, JMethod method)
    {
        JType ex = context.getCodeModel()._ref(UnsupportedOperationException.class);
        method.body()._throw(JExpr._new(ex));
    }

    @Override
    protected ClassType getClassType()
    {
        return ClassType.CLASS;
    }
}
