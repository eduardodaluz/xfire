package org.codehaus.xfire.transport.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
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
    private String encoding = "UTF-8";
    private String urlString;
    private HttpURLConnection urlConn;
    private OutputStream out;
    private InputStream is;
    
    public InMessage open() throws IOException, XFireFault
    {
        URL url = new URL(urlString);
        urlConn = createConnection(url);

        try
        {
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("POST");
            
            // Specify the content type.
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            // Specify content type and encoding
            // If content encoding is not explicitly specified
            // ISO-8859-1 is assumed
            urlConn.setRequestProperty("Content-type", "text/xml; charset=" + encoding);

            urlConn.setRequestProperty("User-Agent", "XFire Client +http://xfire.codehaus.org");
            urlConn.setRequestProperty("Accept", "text/xml; text/html");

            
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
            
            InMessage inMsg = new InMessage(STAXUtils.createXMLStreamReader(is, encoding), urlString);
            
            return inMsg;
        }
        catch (MalformedURLException me)
        {
            throw new XFireRuntimeException("Bad URL.", me);
        }
    }

    public void send(OutMessage msg) throws IOException, XFireFault
    {
        out = urlConn.getOutputStream();
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, encoding);

        msg.getSerializer().writeMessage(msg, writer, null);
    }
    
    public void close()
    {
        try
        {
            out.write('\n');

            out.flush();
            out.close();
            
            if (is != null)
                is.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
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
     * @return Returns the charset.
     */
    public String getEncoding()
    {
        return encoding;
    }

    /**
     * @param charset The charset to set.
     */
    public void setEncoding(String charset)
    {
        this.encoding = charset;
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
}
