package org.codehaus.xfire.wsdl11.builder;

import org.codehaus.xfire.service.Service;

/**
 * Information used when building WSDL 1.1 documents.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WSDLBuilderInfo
{
    private String serviceName;
    private String portType;
    private String targetNamespace;
    public static final String KEY = WSDLBuilderInfo.class.getName();
    
    public WSDLBuilderInfo(Service service)
    {
        serviceName = service.getName();
        portType = service.getName() + "PortType";
        targetNamespace = service.getServiceInfo().getName().getNamespaceURI();
    }
    
    public String getTargetNamespace()
    {
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace)
    {
        this.targetNamespace = targetNamespace;
    }

    public String getServiceName()
    {
        return serviceName;
    }
    
    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    public String getPortType()
    {
        return portType;
    }

    public void setPortType(String portType)
    {
        this.portType = portType;
    }
}
