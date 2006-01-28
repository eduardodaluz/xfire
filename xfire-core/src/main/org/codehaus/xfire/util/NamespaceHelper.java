package org.codehaus.xfire.util;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.XFireRuntimeException;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Namespace utilities.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class NamespaceHelper
{
    /**
     * Create a unique namespace uri/prefix combination.
     *
     * @param nsUri
     * @return The namespace with the specified URI.  If one doesn't exist, one is created.
     */
    public static String getUniquePrefix(Element element, String namespaceURI)
    {
        String prefix = getPrefix(element, namespaceURI);
        
        if (prefix == null)
        {
            prefix = getUniquePrefix(element);
            element.addNamespaceDeclaration(Namespace.getNamespace(prefix, namespaceURI));
        }
        return prefix;
    }

    public static String getPrefix(Element element, String namespaceURI)
    {
        if (element.getNamespaceURI().equals(namespaceURI)) return element.getNamespacePrefix();
        
        List namespaces = element.getAdditionalNamespaces();
        
        for (Iterator itr = namespaces.iterator(); itr.hasNext();)
        {
            Namespace ns = (Namespace) itr.next();
            
            if (ns.getURI().equals(namespaceURI)) return ns.getPrefix();
        }
        
        if (element.getParentElement() != null)
            return getPrefix(element.getParentElement(), namespaceURI);
        else
            return null;
    }
    
    public static void getPrefixes(Element element, String namespaceURI, List prefixes)
    {
        if (element.getNamespaceURI().equals(namespaceURI)) 
            prefixes.add(element.getNamespacePrefix());
        
        List namespaces = element.getAdditionalNamespaces();
        
        for (Iterator itr = namespaces.iterator(); itr.hasNext();)
        {
            Namespace ns = (Namespace) itr.next();
            
            if (ns.getURI().equals(namespaceURI)) 
                prefixes.add(ns.getPrefix());
        }
        
        if (element.getParentElement() != null)
            getPrefixes(element.getParentElement(), namespaceURI, prefixes);
    }
    
    private static String getUniquePrefix(Element el)
    {
        int n = 1;

        while (true)
        {
            String nsPrefix = "ns" + n;

            if (el.getNamespace(nsPrefix) == null)
                return nsPrefix;

            n++;
        }
    }

    /**
     * Create a unique namespace uri/prefix combination.
     *
     * @param nsUri
     * @return The namespace with the specified URI.  If one doesn't exist, one is created.
     * @throws XMLStreamException
     */
    public static String getUniquePrefix(XMLStreamWriter writer, String namespaceURI, boolean declare)
            throws XMLStreamException
    {
        String prefix = writer.getPrefix(namespaceURI);
        if (prefix == null)
        {
            prefix = getUniquePrefix(writer);

            if (declare)
                writer.writeNamespace(prefix, namespaceURI);
        }
        return prefix;
    }

    public static String getUniquePrefix(XMLStreamWriter writer)
    {
        int n = 1;

        while (true)
        {
            String nsPrefix = "ns" + n;

            if (writer.getNamespaceContext().getNamespaceURI(nsPrefix) == null)
            {
                return nsPrefix;
            }

            n++;
        }
    }

    /**
     * Generates the name of a XML namespace from a given class name and protocol. The returned namespace will take the
     * form <code>protocol://domain</code>, where <code>protocol</code> is the given protocol, and <code>domain</code>
     * the inversed package name of the given class name.
     * <p/>
     * For instance, if the given class name is <code>org.codehaus.xfire.services.Echo</code>, and the protocol is
     * <code>http</code>, the resulting namespace would be <code>http://services.xfire.codehaus.org</code>.
     *
     * @param className the class name
     * @param protocol  the protocol (eg. <code>http</code>)
     * @return the namespace
     */
    public static String makeNamespaceFromClassName(String className, String protocol)
    {
        int index = className.lastIndexOf(".");

        if (index == -1)
        {
            return protocol + "://" + "DefaultNamespace";
        }

        String packageName = className.substring(0, index);

        StringTokenizer st = new StringTokenizer(packageName, ".");
        String[] words = new String[st.countTokens()];

        for (int i = 0; i < words.length; ++i)
        {
            words[i] = st.nextToken();
        }

        StringBuffer sb = new StringBuffer(80);

        for (int i = words.length - 1; i >= 0; --i)
        {
            String word = words[i];

            // seperate with dot
            if (i != words.length - 1)
            {
                sb.append('.');
            }

            sb.append(word);
        }

        return protocol + "://" + sb.toString();
    }

    /**
     * Reads a QName from the element text. Reader must be positioned at the
     * start tag.
     * @param reader
     * @return
     * @throws XMLStreamException
     */
    public static QName readQName(XMLStreamReader reader) 
        throws XMLStreamException
    {
        String value = reader.getElementText();
        if (value == null) return null;
        
        int index = value.indexOf(":");
        
        if (index == -1)
        {
            return new QName(value);
        }
        
        String prefix = value.substring(0, index);
        String localName = value.substring(index+1);
        String ns = reader.getNamespaceURI(prefix);
        
        if (ns == null || localName == null)
        {
            throw new XFireRuntimeException("Invalid QName in mapping: " + value);
        }
        
        return new QName(ns, localName, prefix);
    }
    
    public static QName createQName(Element e, String value, String defaultNamespace)
    {
        if (value == null) return null;
        
        int index = value.indexOf(":");
        
        if (index == -1)
        {
            return new QName(defaultNamespace, value);
        }
        
        String prefix = value.substring(0, index);
        String localName = value.substring(index+1);
        String ns = e.getNamespace(prefix).getURI();
        
        if (ns == null || localName == null)
            throw new XFireRuntimeException("Invalid QName in mapping: " + value);
        
        return new QName(ns, localName, prefix);
    }
}
