package org.codehaus.xfire.util.stax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.codehaus.xfire.util.DOMUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class W3CDOMStreamReader
    extends DOMStreamReader
{
    /**
     * TODO: use property from superclass
     */
    private Node content;

    private List uris = new ArrayList();

    private List prefixes = new ArrayList();

    private Map attributes = new HashMap();
    
    /**
     *  Find name spaces declaration in atrributes and move them to separate collection. 
     */
    private void processNamespaces(Element element)
    {
        uris.clear();
        prefixes.clear();
        
        NamedNodeMap nodes = element.getAttributes();
        List nodeToRemove = new ArrayList();
        
        String baseURI = element.getBaseURI();
        String nsURI = element.getNamespaceURI();
        String ePrefix = element.getPrefix();
        if(ePrefix == null ){
            ePrefix = "";
        }
        if( nsURI != null ){
            uris.add(nsURI);
            prefixes.add(ePrefix);
        }
        
        for (int i = 0; i < nodes.getLength(); i++)
        {
            Node node = nodes.item(i);
            String prefix = node.getPrefix();
            String localName = node.getLocalName();
            String value = node.getNodeValue();
            String name = node.getNodeName();
            String uri = node.getNamespaceURI();
            // HACK!!! for xmlns="value" attribute
            // TODO: REIMPLEMENT THIS
            if (prefix == null && ("xmlns".equals(name) || "xmlns".equals(localName)))
            {
                prefix = "xmls";
                localName = "";
            }
            if (prefix != null && !prefixes.contains(localName))
            {
                uris.add(uri);
                prefixes.add(prefix);
                
            }
        }

        for (int j = 0; j < nodeToRemove.size(); j++)
        {
            Node rNode = (Node) nodeToRemove.get(j);
            if (rNode.getNamespaceURI() != null)
            {
                nodes.removeNamedItemNS(rNode.getNamespaceURI(), rNode.getLocalName());
            }
            else
            {
                nodes.removeNamedItem(rNode.getNodeName());
            }
        }

        
        
    }

    /**
     * @param element
     */
    public W3CDOMStreamReader(Element element)
    {
        super(new ElementFrame(element));

        processNamespaces(element);
    }

    protected void endElement()
    {
        super.endElement();
    }

    Element getCurrentElement()
    {
        return (Element) getCurrentFrame().element;
    }

    protected ElementFrame getChildFrame(int currentChild)
    {
        return new ElementFrame(getCurrentElement().getChildNodes().item(currentChild));
    }

    protected int getChildCount()
    {
        return getCurrentElement().getChildNodes().getLength();
    }

    protected int moveToChild(int currentChild)
    {
        this.content = getCurrentElement().getChildNodes().item(currentChild);
        
        if (content instanceof Text)
            return CHARACTERS;
        else if (content instanceof Element)
        {
            processNamespaces((Element) content);
            return START_ELEMENT;
        }
        else if (content instanceof CDATASection)
                    return CHARACTERS;
        else if (content instanceof Comment)
            return CHARACTERS;
        else if (content instanceof EntityReference)
            return ENTITY_REFERENCE;
        
        throw new IllegalStateException();
    }

    public String getElementText()
        throws XMLStreamException
    {
        return DOMUtils.getContent(content);
    }

    public String getNamespaceURI(String prefix)
    {
        int index = prefixes.indexOf(prefix);
        if (index == -1) return null;
        
        return (String) uris.get(index);
    }

    public String getAttributeValue(String ns, String local)
    {
        Attr at;
        if (ns == null || ns.equals("")) 
            at = getCurrentElement().getAttributeNode(local);
        else
            at = getCurrentElement().getAttributeNodeNS(ns, local);

        if (at == null) return null;

        return DOMUtils.getContent(at);
    }

    public int getAttributeCount()
    {
        return getCurrentElement().getAttributes().getLength();
    }

    Attr getAttribute(int i)
    {
        return (Attr) getCurrentElement().getAttributes().item(i);
    }
    
    private String getLocalName(Attr attr ){
        
        String name= attr.getLocalName();
        if( name == null ){
            name = attr.getNodeName();
        }
        return name;
    }
    public QName getAttributeName(int i)
    {
        Attr at = getAttribute(i);
        
        String prefix = at.getPrefix();
        String ln = getLocalName(at); 
            //at.getNodeName();
        String ns = at.getNamespaceURI();
        
        if (prefix == null)
        {
            return new QName(ns, ln);
        }
        else
        {
            return new QName(ns, ln, prefix);
        }
    }

    public String getAttributeNamespace(int i)
    {
        return getAttribute(i).getNamespaceURI();
    }

    public String getAttributeLocalName(int i)
    {
        Attr attr  = getAttribute(i);
        String name = getLocalName(attr);
        return name;
    }

    public String getAttributePrefix(int i)
    {
        return getAttribute(i).getPrefix();
    }

    public String getAttributeType(int i)
    {
        return toStaxType(getAttribute(i).getNodeType());
    }

    public static String toStaxType(short jdom)
    {
        switch(jdom)
        {
        default: return null;
        }     
    }
    
    public String getAttributeValue(int i)
    {
        return getAttribute(i).getValue();
    }

    public boolean isAttributeSpecified(int i)
    {
        return getAttribute(i).getValue() != null;
    }

    public int getNamespaceCount()
    {
        return uris.size();
    }

    public String getNamespacePrefix(int i)
    {
        return (String) prefixes.get(i);
    }

    public String getNamespaceURI(int i)
    {
        return (String) uris.get(i);
    }

    public NamespaceContext getNamespaceContext()
    {
        throw new UnsupportedOperationException();
    }

    public String getText()
    {
        return DOMUtils.getContent(getCurrentElement());
    }

    public char[] getTextCharacters()
    {
        return getText().toCharArray();
    }

    public int getTextStart()
    {
        return 0;
    }

    public int getTextLength()
    {
        return getText().length();
    }

    public String getEncoding()
    {
        return null;
    }

    public QName getName()
    {
        Element el = getCurrentElement();
        
        String prefix = getPrefix();
        String ln = getLocalName();
        
        if (prefix == null)
        {
            return new QName(el.getNamespaceURI(), ln);
        }
        else
        {
            return new QName(el.getNamespaceURI(), ln, prefix);
        }
    }

    public String getLocalName()
    {
        return getCurrentElement().getLocalName();
    }

    public String getNamespaceURI()
    {
        return getCurrentElement().getNamespaceURI();
    }

    public String getPrefix()
    {
        String prefix  = getCurrentElement().getPrefix();
        if( prefix == null ){
            prefix = "";
        }
        return prefix;
    }

    public String getPITarget()
    {
        throw new UnsupportedOperationException();
    }

    public String getPIData()
    {
        throw new UnsupportedOperationException();
    }
}
