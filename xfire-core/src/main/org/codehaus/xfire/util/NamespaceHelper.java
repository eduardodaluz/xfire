package org.codehaus.xfire.util;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;

/**
 * DOM4J Namespace functions.
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
}
