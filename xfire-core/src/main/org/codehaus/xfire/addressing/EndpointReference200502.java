package org.codehaus.xfire.addressing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.codehaus.xfire.util.NamespaceHelper;
import org.codehaus.yom.Element;
import org.codehaus.yom.Elements;

/**
 * A WS-Addressing endpoint reference.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class EndpointReference200502
    implements WSAConstants, EndpointReference
{
    private String address;
    private QName interfaceName;
    private QName serviceName;
    private String endpointName;
    private Element eprElement;
    private String version;
    private Element policies;
    private List anyContent;
    
    /**
     * Create an endpoint reference for the latest WS-A version.
     * @param version
     * @see org.codehaus.xfire.addressing.WSAConstants
     */    
    public EndpointReference200502(String address)
    {
        this(address, WSA_NAMESPACE_200502);
    }
    
    /**
     * Create an endpoint reference for the specified WS-A version.
     * @param version
     * @see org.codehaus.xfire.addressing.WSAConstants
     */
    public EndpointReference200502(String address, String version)
    {
        this.address = address;
        this.version = version;
    }
    
    public EndpointReference200502(Element eprElement)
    {
        Elements elements = eprElement.getChildElements();
        version = eprElement.getNamespaceURI();
        
        for (int i = 0; i < elements.size(); i++)
        {
            Element e = elements.get(i);
            if (e.getNamespaceURI().equals(version))
            {
                if (e.getLocalName().equals(WSA_ADDRESS))
                {
                    address = e.getValue();
                }
                else if (e.getLocalName().equals(WSA_SERVICE_NAME))
                {
                    serviceName = elementToQName(e);
                    endpointName = e.getAttributeValue(WSA_ENDPOINT_NAME, version);
                }
                else if (e.getLocalName().equals(WSA_INTERFACE_NAME))
                {
                    interfaceName = elementToQName(e);
                }
                else if (e.getLocalName().equals(WSA_POLICIES))
                {
                    policies = e;
                }
                else
                {
                    if (anyContent == null)
                        anyContent = new ArrayList();
                    
                    anyContent.add(e);
                }
            }
            
        }
    }
    
    protected QName elementToQName(Element el)
    {
        String value = el.getValue();
        int colon = value.indexOf(":");
        if (colon > -1)
        {
            String prefix = value.substring(0, colon);
            String local = value.substring(colon+1);
            String uri = el.getNamespaceURI(prefix);
            return new QName(uri, local, prefix);
        }
        else
        {
            String uri = el.getNamespaceURI();
            return new QName(value, uri);
        }
    }

    public void writeToElement(Element root)
    {
        Element addEl = new Element(WSA_ADDRESS_QNAME, getVersion());
        addEl.appendChild(address);
        root.appendChild(addEl);
        
        if (interfaceName != null)
        {
            Element intfEl = new Element(WSA_INTERFACE_NAME_QNAME, getVersion());
            intfEl.appendChild(qnameToString(root, getInterfaceName()));
            root.appendChild(intfEl);
        }
        
        if (serviceName != null)
        {
            Element svcEl = new Element(WSA_SERVICE_NAME_QNAME, getVersion());
            svcEl.appendChild(qnameToString(root, getServiceName()));
            root.appendChild(svcEl);
        }
    }

    private String qnameToString(Element root, QName qname)
    {
        String prefix = NamespaceHelper.getUniquePrefix(root, qname.getNamespaceURI());
        
        return prefix + ":" + qname.getLocalPart();
    }

    /**
     * @return
     */
    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    /**
     * @return
     */
    public String getEndpointName()
    {
        return endpointName;
    }

    public void setEndpointName(String endpointName)
    {
        this.endpointName = endpointName;
    }

    public Element getEPRElement()
    {
        return eprElement;
    }

    public void setEPRElement(Element eprElement)
    {
        this.eprElement = eprElement;
    }

    /**
     * @return
     */
    public QName getInterfaceName()
    {
        return interfaceName;
    }

    public void setInterfaceName(QName interfaceName)
    {
        this.interfaceName = interfaceName;
    }

    /**
     * @return
     */
    public QName getServiceName()
    {
        return serviceName;
    }
 
    public void setServiceName(QName serviceName)
    {
        this.serviceName = serviceName;
    }
    
    public Element getPolicies()
    {
        return policies;
    }
    
    public Element createPoliciesElement()
    {
        if (policies == null)
        {
            policies = new Element(WSA_POLICIES_QNAME, getVersion());
        }
        return policies;
    }
    
    public void setPolicies(Element policies)
    {
        this.policies = policies;
    }
    

    public String getVersion()
    {
        return version;
    }

    /**
     * @return
     */
    public List getAnyContent()
    {
        return anyContent;
    }

    public void setAnyContent(List anyContent)
    {
        this.anyContent = anyContent;
    }
}
