package org.codehaus.xfire.jaxws.gen;

import javax.xml.ws.WebFault;

import org.codehaus.xfire.gen.GenerationContext;
import org.codehaus.xfire.gen.GenerationException;
import org.codehaus.xfire.gen.SchemaSupport;
import org.codehaus.xfire.service.MessagePartInfo;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;

public class ServiceStubGenerator
    extends org.codehaus.xfire.gen.jsr181.ServiceStubGenerator
{
    @Override
    protected JClass generateExceptionClass(GenerationContext context, MessagePartInfo part, JMethod method) 
        throws GenerationException
    {
        JCodeModel model = context.getCodeModel();
        SchemaSupport schema = context.getSchemaGenerator();
        
        String name = javify(part.getName().getLocalPart());
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        JType paramType = schema.getType(context, part.getName(), part.getSchemaType().getSchemaType());

        String clsName = getPackage(getCurrentService().getName(), context) + "." + name;
        JDefinedClass exCls;
        try 
        {
            exCls = model._class(clsName);
        } 
        catch (JClassAlreadyExistsException e) {
            return model.ref(clsName);
        }
        
        exCls._extends(Exception.class);
        JAnnotationUse webFaultAnn = exCls.annotate(WebFault.class);
        webFaultAnn.param("name", part.getName().getLocalPart());
        webFaultAnn.param("targetNamespace", part.getName().getNamespaceURI());
        
        JFieldVar faultInfoVar = exCls.field(JMod.PRIVATE, paramType, "faultInfo");
        
        JMethod getFaultInfo = exCls.method(JMod.PUBLIC, paramType, "getFaultInfo");
        getFaultInfo.body()._return(JExpr.ref("faultInfo"));

        JMethod cons = exCls.constructor(JMod.PUBLIC);
        cons.param(String.class, "message");
        cons.param(paramType, "faultInfo");
        cons.body().invoke("super").arg(JExpr.ref("message"));
        cons.body().assign(JExpr.refthis("faultInfo"), JExpr.ref("faultInfo"));
        
        cons = exCls.constructor(JMod.PUBLIC);
        cons.param(String.class, "message");
        cons.param(paramType, "faultInfo");
        cons.param(Throwable.class, "t");
        cons.body().invoke("super").arg(JExpr.ref("message")).arg(JExpr.ref("t"));
        cons.body().assign(JExpr.refthis("faultInfo"), JExpr.ref("faultInfo"));
        
        return exCls;
    }
}
