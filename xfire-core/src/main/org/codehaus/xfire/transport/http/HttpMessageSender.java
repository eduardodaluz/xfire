package org.codehaus.xfire.transport.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.util.STAXUtils;

/**
 * Http Sender
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class HttpMessageSender
{
    private String username;
    private String password;
    private String encoding;
    private String urlString;
    private HttpURLConnection urlConn;
    private InputStream is;
    private String action;

    public HttpMessageSender(String urlString, String encoding)
    {
        this.urlString = urlString;
        this.encoding = encoding;
    }
    
    public void open() throws IOException, XFireFault
    {
        URL url = new URL(urlString);
        urlConn = createConnection(url);
        
        urlConn.setDoInput(true);
        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        urlConn.setRequestMethod("POST");
        
        // Specify the content type.
        urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
        urlConn.setRequestProperty("User-Agent", "XFire Client +http://xfire.codehaus.org");
        urlConn.setRequestProperty("Accept", "text/xml; text/html");
        urlConn.setRequestProperty("Content-type", "text/xml; charset=" + encoding);
        
        String action = getAction();
        if (action == null)
            action = "";
        
        urlConn.setRequestProperty( "SOAPAction", "\"" + action + "\"");
    }

    public OutputStream getOutputStream() throws IOException, XFireFault
    {
        return urlConn.getOutputStream();
    }
    
    public InMessage getInMessage() throws IOException
    {
        try
        {
            is = urlConn.getInputStream();
        }
        catch (IOException ioe)
        {
            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR)
            {
                is = urlConn.getErrorStream();
            }
        }

        return new InMessage(STAXUtils.createXMLStreamReader(is, encoding), urlString);
    }

    public void close() throws XFireException
    {
        
        try
        {
            if (is != null)
                is.close();
        }
        catch (IOException e)
        {
            throw new XFireException("Couldn't close stream.", e);
        }
        finally
        {
            if (urlConn != null)
                urlConn.disconnect();
        }
    }

    private HttpURLConnection createConnection(URL url)
        throws IOException
    {
        return (HttpURLConnection) url.openConnection();
    }

    /**
     * @return Returns the url.
     */
    public String getUrl()
    {
        return urlString;
    }

    /**
     * @param url The url to set.
     */
    public void setUrl(String url)
    {
        this.urlString = url;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return Returns the username.
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username The username to set.
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }
    
}
