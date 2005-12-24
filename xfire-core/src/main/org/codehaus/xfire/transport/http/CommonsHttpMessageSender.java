package org.codehaus.xfire.transport.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.mail.MessagingException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpVersion;
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
import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.attachments.JavaMailAttachments;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.util.STAXUtils;

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
        throws IOException, XFireException
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
        
        postMethod = new PostMethod(getUri());
        
        String username = (String) context.getContextualProperty(Channel.USERNAME);
        if (username != null)
        {
            String password = (String) context.getContextualProperty(Channel.PASSWORD);
            client.getParams().setAuthenticationPreemptive(true);
            getHttpState().setCredentials(AuthScope.ANY, 
                                          new UsernamePasswordCredentials(username, password));
        }
        
        if (getSoapAction() != null)
        {
            postMethod.setRequestHeader("SOAPAction", getQuotedSoapAction());
        }
        
        Attachments atts = getMessage().getAttachments();
        if (atts != null && atts.size() > 0)
        {
            HttpChannel.writeAttachmentBody(context, getMessage());
            
            postMethod.setRequestHeader("Content-Type", atts.getContentType());
        }
        else
        {
            postMethod.setRequestHeader("Content-Type", HttpChannel.getSoapMimeType(getMessage()));
        }
    }

    public void send()
        throws HttpException, IOException, XFireException
    {
        RequestEntity requestEntity;
        
        /**
         * Lots of HTTP servers don't handle chunking correctly, so its turned off by default.
         */
        boolean chunkingOn = Boolean.valueOf((String) getMessageContext()
                .getContextualProperty(HttpTransport.CHUNKING_ENABLED)).booleanValue();
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

    public boolean hasResponse()
    {
        String ct = postMethod.getResponseHeader("Content-Type").getValue();

        return ct != null && ct.length() > 0;
    }
    
    private HttpState getHttpState()
    {
        HttpState state = (HttpState)httpState.get();

        if( null == state )
        {
            state = new HttpState();
            httpState.set( state );
        }

        return state;
    }

    private RequestEntity getByteArrayRequestEntity()
        throws IOException, XFireException
    {
        OutMessage message = getMessage();
        MessageContext context = getMessageContext();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(bos, message.getEncoding());

        Attachments atts = message.getAttachments();
        if (atts != null && atts.size() > 0)
        {
            atts.write(bos);
        }
        else
        {
            HttpChannel.writeWithoutAttachments(context, message, bos);
        }
        
        return new ByteArrayRequestEntity(bos.toByteArray());
    }
    
    public InMessage getInMessage()
        throws IOException
    {
        String ct = postMethod.getResponseHeader("Content-Type").getValue();
        InputStream in = postMethod.getResponseBodyAsStream();
        if (ct.toLowerCase().indexOf("multipart/related") != -1)
        {
            try
            {
                Attachments atts = new JavaMailAttachments(in, ct);

                InputStream msgIs = atts.getSoapMessage().getDataHandler().getInputStream();
                InMessage msg = new InMessage(STAXUtils.createXMLStreamReader(msgIs, getEncoding()), getUri());
                msg.setAttachments(atts);
                return msg;
            }
            catch (MessagingException e)
            {
                log.error(e);
                throw new IOException(e.getMessage());
            }
        }
        else
        {
            return new InMessage(STAXUtils.createXMLStreamReader(in, getEncoding()), getUri());
        }
        
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
