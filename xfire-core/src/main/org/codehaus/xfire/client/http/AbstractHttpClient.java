package org.codehaus.xfire.client.http;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.XFireFault;

/**
 * Common functionality for the SOAP and Rest HTTP clients.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public abstract class AbstractHttpClient
{
    public final static String SOAP11_ENVELOPE_NS = "http://schemas.xmlsoap.org/soap/envelope/";

    public final static String SOAP12_ENVELOPE_NS = "http://www.w3.org/2003/05/soap-envelope";

    private String username;
    private String password;
    private String encoding = "UTF-8";
    private String urlString;

    public void invoke()
            throws IOException, XFireFault
    {
        URL url = new URL(urlString);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

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

            writeHeaders(urlConn);

            OutputStream out = urlConn.getOutputStream();
            writeRequest(out);
            out.write('\n');

            out.flush();
            out.close();

            Reader reader = null;
            try
            {
                reader = new InputStreamReader(urlConn.getInputStream());
                readResponse(reader);
            }
            catch (IOException ioe)
            {
                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR)
                {
                    reader = new InputStreamReader(urlConn.getErrorStream());
                    readResponse(reader);
                }
            }
            finally
            {
                if (reader != null)
                    reader.close();
            }

        }
        catch (MalformedURLException me)
        {
            throw new XFireRuntimeException("Bad URL.", me);
        }
        finally
        {
            urlConn.disconnect();
        }
    }

    protected void writeHeaders(URLConnection urlConn)
    {

    }

    /**
     * @return
     */
    protected void writeRequest(OutputStream out)
    {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        try
        {
            XMLStreamWriter writer = factory.createXMLStreamWriter(out);

            writeRequest(writer);
            writer.close();
        }
        catch (XMLStreamException e)
        {
            throw new XFireRuntimeException("Couldn't parse stream.", e);
        }
    }

    protected abstract void writeRequest(XMLStreamWriter writer)
            throws XMLStreamException;

    /**
     * @param reader
     */
    protected void readResponse(Reader is)
            throws XFireFault
    {
        // Read in Envelope and then delegate header and Body
        XMLInputFactory factory = XMLInputFactory.newInstance();

        try
        {
            readResponse(factory.createXMLStreamReader(is));
        }
        catch (XMLStreamException e)
        {
            throw new XFireRuntimeException("Couldn't parse stream.", e);
        }
    }

    protected abstract void readResponse(XMLStreamReader reader)
            throws XMLStreamException, XFireFault;

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
