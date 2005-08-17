package org.codehaus.xfire.addressing;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.yom.Attribute;
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

    public void writeHeaders(Element root, AddressingHeaders headers)
    {
        final String ns = WSA_NAMESPACE_200502;
        root.addNamespaceDeclaration(WSA_PREFIX, WSA_NAMESPACE_200502);
        
        if (headers.getTo() != null)
        {
            Element to = new Element(WSA_TO_QNAME, ns);
            to.appendChild(headers.getTo());
            root.appendChild(to);
        }
        
        if (headers.getAction() != null)
        {
            Element action = new Element(WSA_ACTION_QNAME, ns);
            action.appendChild(headers.getAction());
            root.appendChild(action);
        }
        
        if (headers.getFaultTo() != null)
        {
            Element faultTo = new Element(WSA_FAULT_TO_QNAME, ns);
            root.appendChild(faultTo);
            
            writeEPR(faultTo, headers.getFaultTo());
        }

        if (headers.getFrom() != null)
        {
            Element from = new Element(WSA_FROM_QNAME, ns);
            root.appendChild(from);
            
            writeEPR(from, headers.getFrom());
        }

        if (headers.getMessageID() != null)
        {
            Element messageId = new Element(WSA_MESSAGE_ID_QNAME, ns);
            messageId.appendChild(headers.getMessageID());
            root.appendChild(messageId);
        }

        if (headers.getRelatesTo() != null)
        {
            Element relatesTo = new Element(WSA_RELATES_TO_QNAME, ns);
            relatesTo.appendChild(headers.getRelatesTo());
            root.appendChild(relatesTo);
            
            if (headers.getRelationshipType() != null)
            {
                String value = qnameToString(root, headers.getRelationshipType());
                relatesTo.addAttribute(new Attribute(WSA_RELATIONSHIP_TYPE, value));
            }
        }
        
        if (headers.getReplyTo() != null)
        {
            Element replyTo = new Element(WSA_REPLY_TO_QNAME, ns);
            root.appendChild(replyTo);
            
            writeEPR(replyTo, headers.getReplyTo());
        }
    }

    public void writeEPR(Element root, EndpointReference epr)
    {
        final String ns = WSA_NAMESPACE_200502;
        
        Element address = new Element(WSA_ADDRESS_QNAME, ns);
        address.appendChild(epr.getAddress());
        root.appendChild(address);
        
        if (epr.getServiceName() != null)
        {
            Element serviceName = new Element(WSA_SERVICE_NAME_QNAME, ns);
            serviceName.appendChild(qnameToString((Element) root.getParent(), epr.getServiceName()));
            root.appendChild(serviceName);
            
            if (epr.getInterfaceName() != null)
            {
                String value = qnameToString((Element) root.getParent(), epr.getInterfaceName());
                serviceName.addAttribute(new Attribute(WSA_INTERFACE_NAME_QNAME, value));
            }
        }
    }

    public String getAnonymousUri()
    {
        return "http://www.w3.org/2005/02/addressing/role/anonymous";
    }
}
