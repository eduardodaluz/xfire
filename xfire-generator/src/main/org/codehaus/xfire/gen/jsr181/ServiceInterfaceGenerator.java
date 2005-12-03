package org.codehaus.xfire.gen.jsr181;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;

import org.codehaus.xfire.gen.GenerationContext;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.soap.SoapBinding;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;


public class ServiceInterfaceGenerator
    extends AbstractServiceGenerator
{
    public final static String SERVICE_INTERFACE = "service.interface";
    private JAnnotationUse wpann;
    private JAnnotationUse wmAnn;
    
    private List<ServiceInfo> serviceInfos = new ArrayList<ServiceInfo>();

    @Override
    public void generate(GenerationContext context)
        throws Exception
    {
        if (context.getService().getEndpoints().size() == 0) return;
        
        super.generate(context);
    }

    protected boolean isWritten(GenerationContext context)
    {
        if (serviceInfos.contains(context.getService().getServiceInfo()))
        {
            return true;
        }
        
        serviceInfos.add(context.getService().getServiceInfo());
        return false;
    }
    
    protected ClassType getClassType()
    {
        return ClassType.INTERFACE;
    }

    protected void annotate(GenerationContext context, Service service, JDefinedClass jc)
    {
        JAnnotationUse ann = jc.annotate(WebService.class);
        
        // TODO: technically, this needs to be the port type name instead
        ann.param("name", service.getServiceInfo().getPortType().getLocalPart());
        ann.param("targetNamespace", service.getTargetNamespace());
        
        context.setProperty(SERVICE_INTERFACE, jc);
    }

    protected void annotate(GenerationContext context, OperationInfo op, JMethod method)
    {
        wmAnn = method.annotate(WebMethod.class);
        wmAnn.param("operationName", op.getName());
    }
    
    protected void annotate(GenerationContext context, OperationInfo op, JMethod method, Binding binding)
    {
        if (binding instanceof SoapBinding)
        {
            String action = ((SoapBinding) binding).getSoapAction(op);

            if (action != null) wmAnn.param("action", action);
        }
    }

    protected void annotateOutParam(MessagePartInfo part, JVar jvar)
    {
        JAnnotationUse wpann = jvar.annotate(WebParam.class);
        wpann.param("name", part.getName().getLocalPart());
        wpann.param("targetNamespace", part.getName().getNamespaceURI());
        wpann.param("mode", WebParam.Mode.OUT);
    }
    
    protected void annotateOutParam(MessagePartInfo part, JVar jvar, Binding binding)
    {
        JAnnotationUse wpann = jvar.annotate(WebParam.class);
        wpann.param("name", part.getName().getLocalPart());
        wpann.param("targetNamespace", part.getName().getNamespaceURI());
        wpann.param("mode", WebParam.Mode.OUT);
        wpann.param("header", true);
    }


    protected void annotateReturnType(JMethod method, MessagePartInfo returnPart)
    {
        JAnnotationUse wrAnn = method.annotate(WebResult.class);
        
        wrAnn.param("name", returnPart.getName().getLocalPart());
        wrAnn.param("targetNamespace", returnPart.getName().getNamespaceURI());
    }

    protected void annotate(MessagePartInfo part, JVar jvar)
    {
        wpann = jvar.annotate(WebParam.class);
        wpann.param("name", part.getName().getLocalPart());
        wpann.param("targetNamespace", part.getName().getNamespaceURI());
    }

    protected void annotate(MessagePartInfo part, JVar jvar, Binding binding)
    {
        wpann = jvar.annotate(WebParam.class);
        wpann.param("name", part.getName().getLocalPart());
        wpann.param("targetNamespace", part.getName().getNamespaceURI());
        wpann.param("header", true);
    }
    
    @Override
    protected String getClassName(GenerationContext context, Service service)
    {
        QName name = service.getServiceInfo().getPortType();
        String pckg = context.getDestinationPackage();
        String clsName = javify(name.getLocalPart());

        return pckg + "." + clsName;
    }
}