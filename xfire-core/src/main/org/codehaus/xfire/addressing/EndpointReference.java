package org.codehaus.xfire.addressing;

import java.util.List;

import javax.xml.namespace.QName;

import org.jdom.Element;

public class EndpointReference extends Element implements WSAConstants
{
    
    private Element element;
    
   // private String address;

    private QName interfaceName;

    private QName serviceName;

    private String endpointName;

    private List policies;

//    private Element any;

   // private List referenceProperties;
    
    //private Element referenceParameters;
    
//    private Element metadata;
    
    public String getAddress()
    {
        return getAddressElem().getValue();
    }
    public Element getAddressElem(){
        return element.getChild(WSA_ADDRESS,element.getNamespace());
    }

    /*public void setAddress(Element address)
    {
        this.address = address;
    }*/

    /*public Element getAny()
    {
        return any;
    }

    public void setAny(Element any)
    {
        this.any = any;
    }*/

    public String getEndpointName()
    {
        return endpointName;
    }

    public void setEndpointName(String endpointName)
    {
        this.endpointName = endpointName;
    }

    public QName getInterfaceName()
    {
        return interfaceName;
    }

    public void setInterfaceName(QName interfaceName)
    {
        this.interfaceName = interfaceName;
    }

    public List getPolicies()
    {
        return policies;
    }

    public void setPolicies(List policies)
    {
        this.policies = policies;
    }

    public QName getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(QName serviceName)
    {
        this.serviceName = serviceName;
    }

    public Element getReferenceParametersElem()
    {
        return  element.getChild(WSA_REFERENCE_PARAMETERS,element.getNamespace());
    }
    public List getReferenceParameters()
    {
        return getReferenceParametersElem().getChildren();
    }
    
    /*public void setReferenceParameters(Element referenceParameters)
    {
        this.referenceParameters = referenceParameters;
    }*/

    public Element getReferencePropertiesElem()
    {
        return element.getChild(WSA_REFERENCE_PROPERTIES,element.getNamespace());
    }
    
    public List getReferenceProperties()
    {
        return getReferencePropertiesElem().getChildren();
    }

    /*public void setReferenceProperties(List referenceProperties)
    {
        this.referenceProperties = referenceProperties;
    }*/

    public Element getMetadataElem()
    {
        return element.getChild(WSA_METADATA, element.getNamespace());
    }

    public List getMetadata()
    {
        return getMetadataElem().getChildren();
    }
    
    /*public void setMetadata(Element metadata)
    {
        this.metadata = metadata;
    }*/

    public Element getElement()
    {
        return element;
    }

    public void setElement(Element element)
    {
        this.element = element;
    }

}