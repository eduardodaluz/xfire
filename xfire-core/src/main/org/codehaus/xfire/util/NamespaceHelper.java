package org.codehaus.xfire.util;

import java.util.StringTokenizer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.yom.Element;

/**
 * Namespace utilities.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class NamespaceHelper
{
    /**
     * Create a unique namespace uri/prefix combination.
     * 
     * @param nsUri
     * @return The namespace with the specified URI.  If one doesn't
     * exist, one is created.
     */
    public static String getUniquePrefix(Element element, String namespaceURI)
    {
        String prefix = element.getNamespacePrefix(namespaceURI);
        if (prefix == null)
        {
            prefix = getUniquePrefix(element);
            element.addNamespaceDeclaration(prefix, namespaceURI);
        }
        return prefix;
    }

    private static String getUniquePrefix( Element el )
    {
        int n = 1;
        
        while(true)
        {
            String nsPrefix = "ns" + n;
            
            if ( el.getNamespaceURI( nsPrefix ) == null )
                return nsPrefix;
            
            n++;
        }
    }
    
    /**
     * Create a unique namespace uri/prefix combination.
     * 
     * @param nsUri
     * @return The namespace with the specified URI.  If one doesn't
     * exist, one is created.
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

    public static String getUniquePrefix( XMLStreamWriter writer )
    {
        int n = 1;
        
        while(true)
        {
            String nsPrefix = "ns" + n;
            
            if ( writer.getNamespaceContext().getNamespaceURI(nsPrefix) == null )
            {
                return nsPrefix;
            }
            
            n++;
        }
    }

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
}
