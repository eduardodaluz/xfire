package org.codehaus.xfire.addressing;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.yom.Element;
import org.codehaus.yom.Elements;

/**
 * A WS-Addressing endpoint reference.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class AddressingHeadersFactory200502
    extends AbstactAddressingHeadersFactory
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
        
        Element faultTo = root.getFirstChildElement(WSA_FAULT_TO, WSA_NAMESPACE_200502);
        if (faultTo != null)
        {
            headers.setFaultTo(createEPR(faultTo));
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
                    
                    Elements polEls = e.getChildElements();
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

    public boolean hasHeaders(Element root)
    {
        return root.getFirstChildElement(WSA_ACTION, WSA_NAMESPACE_200502) != null;
    }

    public void writeEPR(Element root, EndpointReference epr)
    {
        // TODO Auto-generated method stub
        
    }

    public void writeHeaders(Element root, AddressingHeaders headers)
    {
        // TODO Auto-generated method stub
        
    }

}
