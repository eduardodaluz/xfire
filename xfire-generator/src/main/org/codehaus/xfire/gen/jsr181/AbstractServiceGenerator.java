package org.codehaus.xfire.gen.jsr181;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.fault.FaultInfoException;
import org.codehaus.xfire.gen.GenerationContext;
import org.codehaus.xfire.gen.GenerationException;
import org.codehaus.xfire.gen.GeneratorPlugin;
import org.codehaus.xfire.gen.SchemaSupport;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public abstract class AbstractServiceGenerator
    extends AbstractPlugin
    implements GeneratorPlugin
{
    private static final Log log = LogFactory.getLog(AbstractServiceGenerator.class);

    private Service currentService;
    
    public void generate(GenerationContext context)
        throws Exception
    {
        for (Iterator itr = context.getServices().values().iterator(); itr.hasNext();)
        {
            List services = (List) itr.next();
            for (Iterator sitr = services.iterator(); sitr.hasNext();)
                generate(context, (Service) sitr.next());
        }
    }
    
    public void generate(GenerationContext context, Service service)
        throws Exception
    {
        setCurrentService(service);
        
        if (!isWritten(context));

        ServiceInfo serviceInfo = service.getServiceInfo();

        String clsName = getClassName(context, service);
        log.info("Creating class " + clsName);

        File classFile = new File(context.getOutputDirectory(), clsName.replace('.', File.separatorChar) + ".java");
        
        if (classFile.exists() && !overwriteClass(context, service, clsName, classFile))
        {
            return;
        }
        
        JDefinedClass jc = context.getCodeModel()._class(clsName, getClassType());
        
        SchemaSupport schema = context.getSchemaGenerator();
        
        annotate(context, service, jc);
        
        // Process operations
        for (Iterator itr = serviceInfo.getOperations().iterator(); itr.hasNext();)
        {
            OperationInfo op = (OperationInfo) itr.next();
            
            JType returnType = getReturnType(context, schema, op);
            
            String name = javify(op.getName());
            name = name.substring(0, 1).toLowerCase() + name.substring(1);
            
            JMethod method = jc.method(JMod.PUBLIC, returnType, name);
            
            int param = 0;
            
            annotate(context, op, method);
            
            generateOperation(context, op, method);
        }
    }

    protected boolean overwriteClass(GenerationContext context, Service service, String clsName, File classFile)
    {
        return true;
    }


    public Service getCurrentService()
    {
        return currentService;
    }

    public void setCurrentService(Service currentService)
    {
        this.currentService = currentService;
    }

    protected boolean isWritten(GenerationContext context)
    {
        return false;
    }

    private void generateOperation(GenerationContext context, OperationInfo op, JMethod method) 
        throws GenerationException
    {
        Collection bindings = getCurrentService().getBindings();
        SchemaSupport schema = context.getSchemaGenerator();
        
        List<String> partNames = new ArrayList<String>();
        
        // input parts
        MessageInfo inputMsg = op.getInputMessage();
        for (Iterator pitr = inputMsg.getMessageParts().iterator(); pitr.hasNext();)
        {
            MessagePartInfo part = (MessagePartInfo) pitr.next();
            
            String varName = getUniqueName(javify(part.getName().getLocalPart()), partNames);
            partNames.add(varName);

            JType paramType = schema.getType(context, part.getName(), part.getSchemaType().getSchemaType());
            JVar jvar = method.param(0, paramType, varName);
            
            annotate(part, jvar);
        }
   
        // input parts for each binding
        for (Iterator itr = bindings.iterator(); itr.hasNext();)
        {
            Binding binding = (Binding) itr.next();
            annotate(context, op, method, binding);
            
            List headers = binding.getHeaders(inputMsg).getMessageParts();
            for (Iterator bitr = headers.iterator(); bitr.hasNext();)
            {
                MessagePartInfo part = (MessagePartInfo) bitr.next(); 
                
                String varName = getUniqueName(javify(part.getName().getLocalPart()), partNames);
                partNames.add(varName);

                JType paramType = schema.getType(context, part.getName(), part.getSchemaType().getSchemaType());
                JVar jvar = method.param(0, paramType, varName);
                
                annotate(part, jvar, binding);
            }
        }

        if (op.hasOutput() && op.getOutputMessage().size() > 0)
        {
            MessageInfo outputMsg = op.getOutputMessage();
            Iterator rtitr = outputMsg.getMessageParts().iterator();
            MessagePartInfo returnPart = (MessagePartInfo) rtitr.next();
            
            annotateReturnType(method, returnPart);
            
            for (Iterator itr = bindings.iterator(); itr.hasNext();)
            {
                annotateReturnType(method, returnPart, (Binding) itr.next());
            }

            while (rtitr.hasNext())
            {
                MessagePartInfo part = (MessagePartInfo) rtitr.next();
                
                String varName = getUniqueName(javify(part.getName().getLocalPart()), partNames);
                partNames.add(varName);

                JType paramType = getHolderType(context, part);
                JVar jvar = method.param(0, paramType, varName);
                
                annotateOutParam(part, jvar);
            } 

            // Annotate out message bindings
            for (Iterator itr = bindings.iterator(); itr.hasNext();)
            {
                Binding binding = (Binding) itr.next();
                List headers = binding.getHeaders(outputMsg).getMessageParts();
                for (Iterator bitr = headers.iterator(); bitr.hasNext();)
                {
                    MessagePartInfo part = (MessagePartInfo) bitr.next(); 
                    
                    String varName = getUniqueName(javify(part.getName().getLocalPart()), partNames);
                    partNames.add(varName);

                    JType paramType = getHolderType(context, part);
                    JVar jvar = method.param(0, paramType, varName);

                    annotateOutParam(part, jvar, binding);
                }
            }
        }
        else if (!op.hasOutput())
        {
            annotateOneWay(method);
        }
        
        generateFaults(context, op, method);
    }

    protected void annotateOneWay(JMethod method) 
    {
    }

    protected JType getHolderType(GenerationContext context,MessagePartInfo part)
        throws GenerationException
    {
        JType genericType = context.getSchemaGenerator().getType(context, part.getName(), part.getSchemaType().getSchemaType());
        
        try {
            JClass holder = context.getCodeModel().ref("javax.xml.ws.Holder");
            holder = holder.narrow((JClass) genericType);
            return holder;
        } catch (Exception e) {
            throw new GenerationException("Could not find holder type.", e);
        }
    }

    protected void generateFaults(GenerationContext context, OperationInfo op, JMethod method)
        throws GenerationException
    {
        for (Iterator itr = op.getFaults().iterator(); itr.hasNext();)
        {
            FaultInfo faultInfo = (FaultInfo) itr.next();
            
            List messageParts = faultInfo.getMessageParts();
            /*if (messageParts.size() > 1)
            {
                throw new GenerationException("Operation " + op.getName() + " has a fault " + faultInfo.getName() + 
                                              " with multiple parts. This is not supported at this time.");
            }*/
            
            MessagePartInfo part = (MessagePartInfo) messageParts.get(0);
            
            JClass exCls = generateExceptionClass(context, part, method);
            
            method._throws(exCls);
        }
    }

    protected JClass generateExceptionClass(GenerationContext context, MessagePartInfo part, JMethod method)
        throws GenerationException
    {
        JCodeModel model = context.getCodeModel();
        SchemaSupport schema = context.getSchemaGenerator();
        
        String name = javify(((FaultInfo) part.getContainer()).getName());
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        JType paramType = schema.getType(context, part.getName(), part.getSchemaType().getSchemaType());

        String clsName = getPackage(part.getName(), context) + "." + name + "_Exception";
        JDefinedClass exCls;
        try 
        {
            exCls = model._class(clsName);
        } 
        catch (JClassAlreadyExistsException e) {
            return model.ref(clsName);
        }
        
        exCls._extends(FaultInfoException.class);
        
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
        cons.param(Throwable.class, "t");
        cons.param(paramType, "faultInfo");
        cons.body().invoke("super").arg(JExpr.ref("message")).arg(JExpr.ref("t"));
        cons.body().assign(JExpr.refthis("faultInfo"), JExpr.ref("faultInfo"));
                
        JClass qType = model.ref(QName.class);
        JMethod getName = exCls.method(JMod.PUBLIC + JMod.STATIC, qType, "getFaultName");
        getName.body()._return(
                JExpr._new(qType).arg(part.getName().getNamespaceURI()).arg(part.getName().getLocalPart()));

        return exCls;
    }

    private String getUniqueName(String varName, List<String> partNames)
    {
        if (!partNames.contains(varName)) return varName;
        
        for (int i = 2; true; i++)
        {
            String v2 = varName + i;
            if (!partNames.contains(v2))
            {
                return v2;
            }
        }
    }

    protected abstract String getClassName(GenerationContext context, Service service);

    protected abstract ClassType getClassType();

    protected void annotate(GenerationContext context, OperationInfo op, JMethod method)
    {
    }
    
    protected void annotate(GenerationContext context, OperationInfo op, JMethod method, Binding binding)
    {
    }

    protected void annotateOutParam(MessagePartInfo part, JVar jvar)
    {
    }
    
    protected void annotateOutParam(MessagePartInfo part, JVar jvar, Binding binding)
    {
    }

    protected void annotateReturnType(JMethod method, MessagePartInfo returnPart)
    {
    }

    protected void annotateReturnType(JMethod method, MessagePartInfo returnPart, Binding binding)
    {
    }
    
    protected void annotate(MessagePartInfo part, JVar jvar)
    {
    }
    
    protected void annotate(MessagePartInfo part, JVar jvar, Binding binding)
    {
    }
    
    /**
     * Find the return type for the operation. If there is no output message void is returned.
     */
    protected JType getReturnType(GenerationContext context, SchemaSupport schema, OperationInfo op)
        throws GenerationException
    {
        JType returnType;
        if (op.hasOutput() && op.getOutputMessage().size() > 0)
        {
            MessagePartInfo returnPart = 
                (MessagePartInfo) op.getOutputMessage().getMessageParts().iterator().next();

            returnType = schema.getType(context, 
                    returnPart.getName(), returnPart.getSchemaType().getSchemaType());
        }
        else
        {
            returnType = context.getCodeModel().VOID;
        }
        
        return returnType;
    }

    protected void annotate(GenerationContext context, Service service, JDefinedClass jc)
    {
    }

    protected void annotate(GenerationContext context, Service service, JDefinedClass jc, Binding binding)
    {
    }

}
