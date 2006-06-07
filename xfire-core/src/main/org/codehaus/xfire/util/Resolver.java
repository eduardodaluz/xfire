package org.codehaus.xfire.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.codehaus.xfire.XFireRuntimeException;

/**
 * Resolves a File, classpath resource, or URL according to the follow rules:
 * <ul>
 * <li>Check to see if a file exists, relative to the base URI.</li>
 * <li>If the file doesn't exist, check the classpath</li>
 * <li>If the classpath doesn't exist, try to create URL from the URI.</li>
 * </ul>
 * @author Dan Diephouse
 */
public class Resolver
{
    private File file;
    private URL url;
    private InputStream is;
    
    public Resolver(String path) throws IOException
    {
        this("", path);
    }
    
    public Resolver(String baseUri, String uri) 
        throws IOException
    {
        if (baseUri != null)
        {
            file = new File(baseUri, uri);
            
            // fall back on a non relative path, if it doesn't exist we'll catch it later
            if (!file.exists())
                file = new File(uri);
        }
        else
        {
            file = new File(uri);
        }
        
        if (file.exists()) 
        {
            url = file.toURL();
            try
            {
                is = new FileInputStream(file);
            }
            catch (FileNotFoundException e)
            {
                throw new XFireRuntimeException("File mysteriously disappeared! " + uri, e);
            }
        }
        else
        {
            url = ClassLoaderUtils.getResource(uri, getClass());
            
            if (url == null)
            {
                try 
                {
                    url = new URL(uri);
                    is = url.openStream();
                }
                catch (MalformedURLException e)
                {
                }
            }
            else
            {
                is = url.openStream();
            }
        }
        
        if (is == null) 
            throw new IOException("Could not find resource '" + uri + 
                                  "' relative to '" + baseUri + "'");
    }
    
    public URL getURL()
    {
        return url;
    }
    
    public InputStream getInputStream()
    {
        return is;
    }
    
    public boolean isFile()
    {
        return file.exists();
    }
    
    public File getFile()
    {
        return file;
    }
}
