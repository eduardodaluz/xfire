package org.codehaus.xfire.transport.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.util.STAXUtils;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Sends a http message via commons http client. To customize the
 * HttpClient parameters, set the property <code>HTTP_CLIENT_PARAMS</code>
 * on the MessageContext for your invocation.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class CommonsHttpMessageSender extends AbstractMessageSender
{
    private static final ThreadLocal httpState = new ThreadLocal();

    private PostMethod postMethod;

    private HttpClient client;

    private static final Log log = LogFactory.getLog(CommonsHttpMessageSender.class);
    
    public static final String HTTP_CLIENT_PARAMS = "httpClient.params";
    public static final String USER_AGENT =  
        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; XFire Client +http://xfire.codehaus.org)";
    
    public CommonsHttpMessageSender(OutMessage message, MessageContext context)
    {
        super(message, context);
    }
    
    public void open()
        throws IOException, XFireFault
    {
        client = new HttpClient();
        
        MessageContext context = getMessageContext();
        
        HttpClientParams params = (HttpClientParams) context.getContextualProperty(HTTP_CLIENT_PARAMS);
        if (params == null)
        {
            params = client.getParams();
            
            client.getParams().setParameter("http.useragent", USER_AGENT);
            client.getParams().setBooleanParameter("http.protocol.expect-continue", true);
            client.getParams().setVersion(HttpVersion.HTTP_1_1);
        }
        else
        {
            client.setParams(params);
        }
        
        String username = (String) context.getProperty(Channel.USERNAME);
        if (username != null)
        {
            String password = (String) context.getProperty(Channel.PASSWORD);
            client.getState().setCredentials(AuthScope.ANY, 
                                             new UsernamePasswordCredentials(username, password));
        }
        
        postMethod = new PostMethod(getUri());
        postMethod.setRequestHeader("Content-Type", "text/xml; charset="+getEncoding());

        if (getSoapAction() != null)
        {
            postMethod.setRequestHeader("SOAPAction", getQuotedSoapAction());
        }
    }

    public void send()
        throws HttpException, IOException
    {
        RequestEntity requestEntity;
        
        /**
         * Lots of HTTP servers don't handle chunking correctly, so its turned off by default.
         */
        boolean chunkingOn = Boolean.valueOf((String) getMessageContext()
                .getProperty(HttpTransport.CHUNKING_ENABLED)).booleanValue();
        if (!chunkingOn)
        {
            requestEntity = getByteArrayRequestEntity();
        }
        else
        {
            requestEntity = new OutMessageRequestEntity(getMessage(), getMessageContext());
        }
        
        getMethod().setRequestEntity(requestEntity);
        
        client.executeMethod(null, postMethod, getHttpState() );
    }

    private HttpState getHttpState()
    {
        HttpState state = (HttpState)httpState.get();

        if( null == state ) {
            state = new HttpState();
            httpState.set( state );
        }

        return state;
    }

    private RequestEntity getByteArrayRequestEntity()
        throws IOException
    {
        OutMessage message = getMessage();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(bos, message.getEncoding());
        try
        {
            message.getSerializer().writeMessage(message, writer, getMessageContext());
            writer.close();
            bos.close();

            return new ByteArrayRequestEntity(bos.toByteArray());
        }
        catch (XFireFault e)
        {
            log.error("Couldn't send message.", e);
            throw new IOException(e.getMessage());
        }
        catch (XMLStreamException e)
        {
            log.error("Couldn't send message.", e);
            throw new IOException(e.getMessage());
        }
    }

    public InMessage getInMessage()
        throws IOException
    {
        InputStream in = postMethod.getResponseBodyAsStream();
        return new InMessage(STAXUtils.createXMLStreamReader(in, getEncoding()), getUri());
    }

    public PostMethod getMethod()
    {
        return this.postMethod;
    }

    public void close()
        throws XFireException
    {
        if (postMethod != null)
            postMethod.releaseConnection();
    }
}
