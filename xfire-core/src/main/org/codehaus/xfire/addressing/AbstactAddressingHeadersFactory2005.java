package org.codehaus.xfire.addressing;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

public abstract class AbstactAddressingHeadersFactory2005
    extends AbstactAddressingHeadersFactory
{

   

    protected abstract Namespace getNamespace();

    public AddressingHeaders createHeaders(Element root)
    {
        AddressingHeaders headers = new AddressingHeaders();

        Namespace wsa = getNamespace();

        Element from = root.getChild(WSA_FROM, wsa);
        if (from != null)
        {
            headers.setFrom(createEPR(from));
        }

        Element replyTo = root.getChild(WSA_REPLY_TO, wsa);
        if (replyTo != null)
        {
            headers.setReplyTo(createEPR(replyTo));
        }

        Element faultTo = root.getChild(WSA_FAULT_TO, wsa);
        if (faultTo != null)
        {
            headers.setFaultTo(createEPR(faultTo));
        }

        Element messageId = root.getChild(WSA_MESSAGE_ID, wsa);
        if (messageId != null)
        {
            headers.setMessageID(messageId.getValue());
        }

        Element to = root.getChild(WSA_TO, wsa);
        if (to != null)
        {
            headers.setTo(to.getValue());
        }

        Element action = root.getChild(WSA_ACTION, wsa);
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

        List elements = eprElement.getChildren();
        String version = eprElement.getNamespaceURI();

        Namespace wsa= getNamespace();
        epr.setElement(eprElement);
        for (int i = 0; i < elements.size(); i++)
        {
            Element e = (Element) elements.get(i);
            if (e.getNamespaceURI().equals(version))
            {
                
                
                /*if (e.getName().equals(WSA_ADDRESS) )
                {
                    Element address = new Element(e.getName(),e.getNamespace());
                    address.setAttributes(e.getAttributes());
                    // TODO : xxx
                   // epr.setAddress(e);
                }
                else*/ 
               
                if (e.getName().equals(WSA_SERVICE_NAME) )
                {
                    epr.setServiceName(elementToQName(e));
                    epr.setEndpointName(e.getAttributeValue(WSA_ENDPOINT_NAME, version));
                }
                else if (e.getName().equals(WSA_INTERFACE_NAME) )
                {
                    epr.setInterfaceName(elementToQName(e) );
                }
                else if (e.getName().equals(WSA_POLICIES) )
                {
                    List policies = new ArrayList();

                    List polEls = e.getChildren();
                    for (int j = 0; j < polEls.size(); j++)
                    {
                        policies.add(polEls.get(j));
                    }
                    epr.setPolicies(policies);
                }/*else if( e.getName().equals(WSA_REFERENCE_PARAMETERS) ){
                    List parameters = new ArrayList();
                    List polEls = e.getChildren();
                    for (int j = 0; j < polEls.size(); j++)
                    {
                        parameters .add(polEls.get(j));
                    }
                    epr.setReferenceParameters(e);
                }*//*else if( e.getName().equals(WSA_METADATA) ){
                    List metadata = new ArrayList();
                    List polEls = e.getChildren();
                    for (int j = 0; j < polEls.size(); j++)
                    {
                        metadata .add(polEls.get(j));
                    }
                    epr.setMetadata(metadata);
                }
*/                /*else
                {
                    if (anyContent == null)
                        anyContent = new ArrayList();

                    anyContent.add(e);
                }*/
            }/*else{
                if (anyContent == null)
                    anyContent = new ArrayList();

                anyContent.add(e);
            }
*/
        }

        /*if (anyContent != null)
        {
            epr.setAny(anyContent);
        }*/

        return epr;
    }

    public boolean hasHeaders(Element root)
    {
        return root.getChild(WSA_ACTION, getNamespace()) != null;
    }

    public void writeHeaders(Element root, AddressingHeaders headers)
    {
        final Namespace ns = getNamespace();

        root.addNamespaceDeclaration(ns);

        if (headers.getTo() != null)
        {
            Element to = new Element(WSA_TO, ns);
            to.addContent(headers.getTo());
            root.addContent(to);
        }

        if (headers.getAction() != null)
        {
            Element action = new Element(WSA_ACTION, ns);
            action.addContent(headers.getAction());
            root.addContent(action);
        }

        if (headers.getFaultTo() != null)
        {
            Element faultTo = new Element(WSA_FAULT_TO, ns);
            root.addContent(faultTo);

            writeEPR(faultTo, headers.getFaultTo());
        }

        if (headers.getFrom() != null)
        {
            Element from = new Element(WSA_FROM, ns);
            root.addContent(from);

            writeEPR(from, headers.getFrom());
        }

        if (headers.getMessageID() != null)
        {
            Element messageId = new Element(WSA_MESSAGE_ID, ns);
            messageId.addContent(headers.getMessageID());
            root.addContent(messageId);
        }

        if (headers.getRelatesTo() != null)
        {
            Element relatesTo = new Element(WSA_RELATES_TO, ns);
            relatesTo.addContent(headers.getRelatesTo());
            root.addContent(relatesTo);

            if (headers.getRelationshipType() != null)
            {
                String value = qnameToString(root, headers.getRelationshipType());
                relatesTo.setAttribute(new Attribute(WSA_RELATIONSHIP_TYPE, value));
            }
        }

        if (headers.getReplyTo() != null)
        {
            Element replyTo = new Element(WSA_REPLY_TO, ns);
            root.addContent(replyTo);

            writeEPR(replyTo, headers.getReplyTo());
        }
    }

    public void writeEPR(Element root, EndpointReference epr)
    {
        final Namespace ns = getNamespace();

        Element address = new Element(WSA_ADDRESS, ns);
        address.addContent(epr.getAddress());
        root.addContent(address);

        if (epr.getServiceName() != null)
        {
            Element serviceName = new Element(WSA_SERVICE_NAME, ns);
            serviceName.addContent(qnameToString((Element) root.getParent(), epr.getServiceName()));
            root.addContent(serviceName);

            if (epr.getInterfaceName() != null)
            {
                String value = qnameToString((Element) root.getParent(), epr.getInterfaceName());
                serviceName.setAttribute(new Attribute(WSA_INTERFACE_NAME, value));
            }
        }
    }

}