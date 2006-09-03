package org.codehaus.xfire.jaxws.gen;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.namespace.QName;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.gen.GenerationContext;
import org.codehaus.xfire.gen.GenerationException;
import org.codehaus.xfire.gen.jsr181.ServiceInterfaceGenerator;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.OperationInfo;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

public class InterfaceGenerator
    extends ServiceInterfaceGenerator
{

    @Override
    protected void annotate(GenerationContext context, Service service, JDefinedClass jc)
    {
        JAnnotationUse ann = jc.annotate(WebService.class);
        
        ann.param("name", service.getServiceInfo().getPortType().getLocalPart());
        // The service interface should have the namespace of the port type
        ann.param("targetNamespace", service.getServiceInfo().getPortType().getNamespaceURI());
        
        service.setProperty(SERVICE_INTERFACE, jc);
        
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
    }

    @Override
    protected void annotate(GenerationContext context, OperationInfo op, JMethod method) 
    {
        super.annotate(context, op, method);
        
        if (op.getService().isWrapped())
        {
            try
            {
                generateWrapperTypes(context, op, method);
            }
            catch (GenerationException e)
            {
                throw new XFireRuntimeException("Could not generate wrapper types.", e);
            }
        }
        
    }

    private void generateWrapperTypes(GenerationContext context, OperationInfo op, JMethod m) 
        throws GenerationException 
    {
        QName reqTypeName = new QName(op.getInputMessage().getName().getNamespaceURI(), op.getName());
        QName resTypeName = new QName(op.getOutputMessage().getName().getNamespaceURI(), op.getName() + "Response");
        
        JType reqType = context.getSchemaGenerator().getType(context, reqTypeName, null);
        JType resType = context.getSchemaGenerator().getType(context, resTypeName, null);
        
        JAnnotationUse reqA = m.annotate(RequestWrapper.class);
        reqA.param("targetNamespace", reqTypeName.getNamespaceURI());
        reqA.param("localName", reqTypeName.getLocalPart());
        reqA.param("className", reqType.fullName());

        JAnnotationUse resA = m.annotate(ResponseWrapper.class);
        resA.param("targetNamespace", resTypeName.getNamespaceURI());
        resA.param("localName", resTypeName.getLocalPart());
        resA.param("className", resType.fullName());
    }
}
