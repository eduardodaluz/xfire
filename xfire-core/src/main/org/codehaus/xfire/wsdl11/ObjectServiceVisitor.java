package org.codehaus.xfire.wsdl11;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Service;

import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.DefaultObjectService;
import org.codehaus.xfire.service.binding.Parameter;
import org.codehaus.xfire.soap.SoapConstants;

/**
 * TODO: I would be great to be able to configure XFire during deployment and
 * write it to the configuration
 */
public class ObjectServiceVisitor
    extends WSDLVisitor
{
    private DefaultObjectService service;

    private BindingProvider provider;

    private Map parameterMap;

    private Map typeMap;

    public ObjectServiceVisitor(Definition definition, 
                                DefaultObjectService service)
    {
        super(definition);

        this.service = service;
        
        // Setup Type Mapping
        service.setStyle(SoapConstants.STYLE_DOCUMENT);
        service.setUse(SoapConstants.USE_LITERAL);
        
        provider = service.getBindingProvider();

        parameterMap = new HashMap();
        typeMap = new HashMap();
    }

    public void configure()
    {
        this.walkTree();
    }

    protected void visit(Part part)
    {
        /*SchemaType type = service.getBindingProvider().getSchemaType(service, param);
        if (type == null)
            throw new XFireRuntimeException("Couldn't find type for " + part.getElementName());
        
        Parameter parameter = new Parameter(part.getElementName(), type.getTypeClass());
        parameterMap.put(part, parameter);
        typeMap.put(part, type.getTypeClass());*/
    }

    protected void visit(Operation wsdlOperation)
    {
        Method method = getMethod(wsdlOperation);

        org.codehaus.xfire.service.binding.Operation xfireOperation = new org.codehaus.xfire.service.binding.Operation(
                method);

        // setup input params
        Collection inParts = wsdlOperation.getInput().getMessage().getParts().values();
        for (Iterator iterator = inParts.iterator(); iterator.hasNext();)
        {
            Part part = (Part) iterator.next();
            Parameter inParameter = (Parameter) parameterMap.get(part);
            xfireOperation.addInParameter(inParameter);
        }

        // setup output param
        Iterator outParts = wsdlOperation.getOutput().getMessage().getParts().values().iterator();
        if (outParts.hasNext())
        {
            Part part = (Part) outParts.next();
            Parameter outParameter = (Parameter) parameterMap.get(part);
            xfireOperation.addOutParameter(outParameter);
        }

        service.addOperation(xfireOperation);
    }

    private Method getMethod(Operation wsdlOperation)
    {
        Input input = wsdlOperation.getInput();
        List parts = input.getMessage().getOrderedParts(wsdlOperation.getParameterOrdering());
        Class[] types = new Class[parts.size()];
        for (int i = 0; i < parts.size(); i++)
        {
            types[i] = (Class) typeMap.get((Part) parts.get(i));
        }

        try
        {
            return service.getServiceClass().getMethod(wsdlOperation.getName(), types);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException("There is no method matching the operation named "
                    + wsdlOperation.getName());
        }
    }

    protected void visit(Service wsdlService)
    {
        service.setDefaultNamespace(wsdlService.getQName().getNamespaceURI());
        service.setName(wsdlService.getQName().getLocalPart());
    }
}
