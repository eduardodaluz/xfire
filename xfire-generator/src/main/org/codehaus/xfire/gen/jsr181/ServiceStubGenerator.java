package org.codehaus.xfire.gen.jsr181;

import java.io.File;

import javax.jws.WebService;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private final static Log log = LogFactory.getLog(ServiceStubGenerator.class);
    
    public static final String SERVICE_STUB = "service.stub";
    
    @Override
    public void generate(GenerationContext context, Service service)
        throws Exception
    {
        if (service.getEndpoints().size() == 0 || !context.isServerStubGenerated()) return;
        
        super.generate(context, service);
    }

    @Override
    protected boolean overwriteClass(GenerationContext context, Service service, String clsName, File classFile)
    {
        if (context.isServerStubOverwritten())
            return true;

        try
        {
            JType type = context.getCodeModel().parseType(clsName);
            service.setProperty(SERVICE_STUB, type);
        }
        catch (ClassNotFoundException e)
        {
            return true;
        }
        
        log.info("Found a pre-existing server stub. It will not be overwritten.");
        
        return false;
    }
    
    @Override
    protected String getClassName(GenerationContext context, Service service)
    {
        QName name = service.getName();
        String pckg = getPackage(service.getName(), context);
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
