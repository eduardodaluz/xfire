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
public class AddressingHeadersFactory200502
    implements WSAConstants, AddressingHeadersFactory
{
    public AddressingHeaders createHeaders(Element root)
    {
        AddressingHeaders headers = new AddressingHeaders();
        
        Element from = root.getFirstChildElement(WSA_FROM, WSA_NAMESPACE_200502);
        if (from != null)
        {
            headers.setFrom(createEPR(from));
        }
        
        Element replyTo = root.getFirstChildElement(WSA_REPLY_TO, WSA_NAMESPACE_200502);
        if (replyTo != null)
        {
            headers.setReplyTo(createEPR(replyTo));
        }
        
        Element messageId = root.getFirstChildElement(WSA_MESSAGE_ID, WSA_NAMESPACE_200502);
        if (messageId != null)
        {
            headers.setMessageID(messageId.getValue());
        }
        
        Element to = root.getFirstChildElement(WSA_TO, WSA_NAMESPACE_200502);
        if (to != null)
        {
            headers.setTo(to.getValue());
        }
        
        Element action = root.getFirstChildElement(WSA_ACTION, WSA_NAMESPACE_200502);
        if (action != null)
        {
            headers.setAction(action.getValue());
        }
        
        return headers;
    }
    
    public EndpointReference createEPR(Element eprElement)
    {
        EndpointReference epr = new EndpointReference();
        
        List anyContent = null;
        
        Elements elements = eprElement.getChildElements();
        String version = eprElement.getNamespaceURI();
        
        for (int i = 0; i < elements.size(); i++)
        {
            Element e = elements.get(i);
            if (e.getNamespaceURI().equals(version))
            {
                if (e.getLocalName().equals(WSA_ADDRESS))
                {
                    epr.setAddress(e.getValue());
                }
                else if (e.getLocalName().equals(WSA_SERVICE_NAME))
                {
                    epr.setServiceName(elementToQName(e));
                    epr.setEndpointName(e.getAttributeValue(WSA_ENDPOINT_NAME, version));
                }
                else if (e.getLocalName().equals(WSA_INTERFACE_NAME))
                {
                    epr.setInterfaceName(elementToQName(e));
                }
                else if (e.getLocalName().equals(WSA_POLICIES))
                {
                    List policies = new ArrayList();
                    
                    Elements polEls = e.getChildElements(WSA_POLICIES);
                    for (int j = 0; j < polEls.size(); j++)
                    {
                        policies.add(polEls.get(j));
                    }
                    epr.setPolicies(policies);
                }
                else
                {
                    if (anyContent == null)
                        anyContent = new ArrayList();
                    
                    anyContent.add(e);
                }
            }
            
        }
        
        if (anyContent != null)
        {
            epr.setAny(anyContent);
        }
        
        return epr;
    }
    
    protected static QName elementToQName(Element el)
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
/*
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
//    }*/

    private static String qnameToString(Element root, QName qname)
    {
        String prefix = NamespaceHelper.getUniquePrefix(root, qname.getNamespaceURI());
        
        return prefix + ":" + qname.getLocalPart();
    }

    public boolean hasHeaders(Element root)
    {
        return root.getFirstChildElement(WSA_ACTION, WSA_NAMESPACE_200502) != null;
    }
}
