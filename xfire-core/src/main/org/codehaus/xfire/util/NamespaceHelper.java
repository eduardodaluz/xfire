package org.codehaus.xfire.util;

import java.util.StringTokenizer;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;

/**
 * Namespace utilities.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class NamespaceHelper
{
    /**
     * @param nsUri
     * @return The namespace with the specified URI.  If one doesn't
     * exist, one is created.
     */
    public static Namespace createNamespace( String prefix, String nsUri )
    {
        return DocumentFactory.getInstance().createNamespace( prefix, nsUri );
    }
    
    /**
     * Create a unique namespace uri/prefix combination.
     * 
     * @param nsUri
     * @return The namespace with the specified URI.  If one doesn't
     * exist, one is created.
     */
    public static Namespace getNamespace( Element el, String nsUri )
    {
        Namespace ns = el.getNamespaceForURI( nsUri );

        if ( ns == null || ns.getPrefix().equals("") )
        {
            ns = DocumentFactory.getInstance().createNamespace( getUniquePrefix( el ), nsUri );
            el.add( ns );
        }
        
        return ns;
    }
    
    private static String getUniquePrefix( Element el )
    {
        int n = 1;
        
        while(true)
        {
            String nsPrefix = "ns" + n;
            
            if ( el.getNamespaceForPrefix( nsPrefix ) == null )
                return nsPrefix;
            
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
