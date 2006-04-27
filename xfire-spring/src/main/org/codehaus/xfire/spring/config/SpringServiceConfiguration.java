package org.codehaus.xfire.spring.config;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.binding.ServiceConfiguration;
import org.codehaus.xfire.soap.SoapConstants;

/**
 * Service configuration information which is provide by Spring beans.
 * 
 * @author Dan Diephouse
 */
public class SpringServiceConfiguration
    extends ServiceConfiguration
{
    private ObjectServiceFactory serviceFactory;
    private List methods;
    private Map method2Op = new HashMap();
    
    public ObjectServiceFactory getServiceFactory()
    {
        return serviceFactory;
    }

    public void setServiceFactory(ObjectServiceFactory serviceFactory)
    {
        this.serviceFactory = serviceFactory;
    }

    public void setMethods(List operations)
    {
        this.methods = operations;
    }

    private MethodBean getMethodBean(Method m)
    {
        if (method2Op.containsKey(m)) 
            return (MethodBean) method2Op.get(m);
        
        if (methods == null) return null;
        
        MethodBean match = null;
        for (Iterator itr = methods.iterator(); itr.hasNext();)
        {
            MethodBean op = (MethodBean) itr.next();
            if (!op.getName().equals(m.getName()))
                continue;

            if (parametersMatch(m, op))
            {
                if (match != null)
                {
                    throw new XFireRuntimeException("Paramters for the operation " + op.getOperationName() + 
                                                    " are under specificied. Please supply additional parameters " +
                                                    "to make Method selection unambiguous.");
                }
                match = op;
            }
        }

        method2Op.put(m, match);
        return match;
    }
    
    private boolean parametersMatch(Method m, MethodBean op)
    {
        List params = op.getParameters();
        if (params == null) return true;
        
        for (Iterator itr = params.iterator(); itr.hasNext();)
        {
            ParameterBean p = (ParameterBean) itr.next();
            int i = p.getIndex();
            
            if (i == Integer.MAX_VALUE)
            {
                String name = (op.getOperationName() != null) ? op.getOperationName() : m.getName();
                throw new XFireRuntimeException("The index for all parameters on operation " + name + 
                                                " must be set!");
            }
            
            if (i >= m.getParameterTypes().length)
                return false;
            
            String clz = p.getClazz();
            if (i >= 0 && clz != null && ! m.getParameterTypes()[i].getName().equals(clz))
            {
                return false;
            }
        }

        return true;
    }

    public Boolean isAsync(Method method)
    {
        MethodBean op = getMethodBean(method);
        if (op != null && op.getMep() != null)
        {
            return new Boolean(op.getMep().equals(SoapConstants.MEP_IN));
        }
        
        return super.isAsync(method);
    }

    public Boolean isHeader(Method method, int j)
    {
        ParameterBean p = getParameter(method, j);
        if (p != null && p.getHeader() != null)
        {
            return p.getHeader();
        }
        
        return super.isHeader(method, j);
    }

    private ParameterBean getParameter(Method method, int j)
    {
        MethodBean op = getMethodBean(method);
        if (op == null) return null;
        
        if (op.getParameters() != null)
        {
            for (Iterator itr = op.getParameters().iterator(); itr.hasNext();)
            {
                ParameterBean p = (ParameterBean) itr.next();
                if (p.getIndex() == j) return p;
            }
        }
        
        return null;
    }

    public Boolean isInParam(Method method, int j)
    {
        ParameterBean p = getParameter(method, j);
        if (p != null && p.getMode() != null)
        {
            return new Boolean(p.getMode().equals("in") || 
                p.getMode().equals("inout"));
        }
        return super.isInParam(method, j);
    }

    public Boolean isOperation(Method method)
    {
        MethodBean op = getMethodBean(method);
        if (op != null && op.isExclude())
        {
            return Boolean.FALSE;
        }
        
        return super.isOperation(method);
    }

    public Boolean isOutParam(Method method, int j)
    {
        ParameterBean p = getParameter(method, j);
        if (p != null && p.getMode() != null)
        {
            return new Boolean(p.getMode().equals("out") || 
                p.getMode().equals("inout"));
        }
        
        return super.isOutParam(method, j);
    }

    public String getAction(OperationInfo op2)
    {
        MethodBean op = getMethodBean(op2.getMethod());
        if (op != null)
        {
            return op.getSoapAction();
        }
        return super.getAction(op2);
    }

    public QName getFaultName(Service service, OperationInfo o, Class exClass, Class beanClass)
    {
        return super.getFaultName(service, o, exClass, beanClass);
    }

    public QName getInParameterName(Service service, OperationInfo op, Method method, int paramNumber, boolean doc)
    {
        ParameterBean p = getParameter(method, paramNumber);
        if (p != null && p.getName() != null)
        {
            return p.getName();
        }
        
        return super.getInParameterName(service, op, method, paramNumber, doc);
    }

    public QName getInputMessageName(OperationInfo op2)
    {
        return super.getInputMessageName(op2);
    }

    public String getMEP(Method method)
    {
        MethodBean op = getMethodBean(method);
        if (op != null)
        {
            return op.getMep();
        }
        return super.getMEP(method);
    }

    public String getOperationName(ServiceInfo service, Method method)
    {
        MethodBean op = getMethodBean(method);
        if (op != null && op.getOperationName() != null)
        {
            return op.getOperationName();
        }
        
        return super.getOperationName(service, method);
    }

    public QName getOutParameterName(Service service, OperationInfo op, Method method, boolean doc)
    {
        ParameterBean p = getParameter(method, -1);
        if (p != null && p.getName() != null)
        {
            return p.getName();
        }
        
        return super.getOutParameterName(service, op, method, doc);
    }

    public QName getOutputMessageName(OperationInfo op)
    {
        // TODO Auto-generated method stub
        return super.getOutputMessageName(op);
    }

    public QName getPortType(Class clazz)
    {
        return super.getPortType(clazz);
    }
}
