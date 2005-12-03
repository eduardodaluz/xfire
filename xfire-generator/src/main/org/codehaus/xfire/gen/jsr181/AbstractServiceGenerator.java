package org.codehaus.xfire.gen.jsr181;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public abstract class AbstractServiceGenerator
    extends AbstractPlugin
    implements GeneratorPlugin
{
    private static final Log log = LogFactory.getLog(AbstractServiceGenerator.class);

    public void generate(GenerationContext context)
        throws Exception
    {
        if (!isWritten(context));
        
        Service service = context.getService();
        ServiceInfo serviceInfo = service.getServiceInfo();

        String clsName = getClassName(context, service);
        log.info("Creating class " + clsName);

        JDefinedClass jc = context.getCodeModel()._class(clsName, getClassType());
        
        SchemaSupport schema = context.getSchemaGenerator();
        
        annotate(context, service, jc);
        
        // Process operations
        for (Iterator itr = serviceInfo.getOperations().iterator(); itr.hasNext();)
        {
            OperationInfo op = (OperationInfo) itr.next();
            
            JType returnType = getReturnType(context, schema, op);
            
            JMethod method = jc.method(JMod.PUBLIC, returnType, javify(op.getName()));
            
            int param = 0;
            
            annotate(context, op, method);
            
            generateOperation(context, op, method);
        }
    }

    protected boolean isWritten(GenerationContext context)
    {
        return false;
    }

    private void generateOperation(GenerationContext context, OperationInfo op, JMethod method) 
        throws GenerationException
    {
        Collection bindings = context.getService().getBindings();
        SchemaSupport schema = context.getSchemaGenerator();
        
        List<String> partNames = new ArrayList<String>();
        
        // input parts
        MessageInfo inputMsg = op.getInputMessage();
        for (Iterator pitr = inputMsg.getMessageParts().iterator(); pitr.hasNext();)
        {
            MessagePartInfo part = (MessagePartInfo) pitr.next();
            
            String varName = part.getName().getLocalPart();
            varName = getUniqueName(varName, partNames);
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
                
                String varName = part.getName().getLocalPart();
                varName = getUniqueName(varName, partNames);
                
                JType paramType = schema.getType(context, part.getName(), part.getSchemaType().getSchemaType());
                JVar jvar = method.param(0, paramType, varName);
                
                annotate(part, jvar, binding);
            }
        }

        if (op.hasOutput())
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
                
                JType paramType = schema.getType(context, part.getName(), null);
                JVar jvar = method.param(0, paramType, part.getName().getLocalPart());
                
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
                    
                    JType paramType = schema.getType(context, part.getName(), part.getSchemaType().getSchemaType());
                    JVar jvar = method.param(0, paramType, part.getName().getLocalPart());
                    
                    annotateOutParam(part, jvar, binding);
                }
            }
        }
        
        for (Iterator itr = op.getFaults().iterator(); itr.hasNext();)
        {
            FaultInfo faultInfo = (FaultInfo) itr.next();
            
            List messageParts = faultInfo.getMessageParts();
            if (messageParts.size() > 0)
            {
                throw new GenerationException("Operation " + op.getName() + " has a fault " + faultInfo.getName() + 
                                              " with multiple parts. This is not supported at this time.");
            }
            
            MessagePartInfo part = (MessagePartInfo) messageParts.get(0);
            
            generateExceptionClass(context, part);
        }
    }

    protected void generateExceptionClass(GenerationContext context, MessagePartInfo part)
    {
        // TODO Auto-generated method stub
        
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
    
    protected JType getReturnType(GenerationContext context, SchemaSupport schema, OperationInfo op)
        throws GenerationException
    {
        JType returnType;
        Iterator rtitr = op.getOutputMessage().getMessageParts().iterator();
        if (rtitr.hasNext())
        {
            MessagePartInfo returnPart = (MessagePartInfo) rtitr.next();
            
            returnType = schema.getType(context, returnPart.getName(), returnPart.getSchemaType().getSchemaType());
        }
        else
        {
            returnType = context.getCodeModel().ref(void.class);
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
