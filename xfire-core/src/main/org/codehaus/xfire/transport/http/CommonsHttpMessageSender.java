package org.codehaus.xfire.transport.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.activation.DataHandler;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.attachments.JavaMailAttachments;
import org.codehaus.xfire.attachments.SimpleAttachment;
import org.codehaus.xfire.attachments.StreamedAttachments;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.util.OutMessageDataSource;
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
    private PostMethod postMethod;

    private HttpClient client;

    private HttpState state;
    
    private static final String GZIP_CONTENT_ENCODING = "gzip";

    public static final String DISABLE_KEEP_ALIVE = "disable-keep-alive";
    public static final String DISABLE_EXPECT_CONTINUE = "disable.expect-continue";
    public static final String HTTP_CLIENT_PARAMS = "httpClient.params";
    public static final String USER_AGENT =  
        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; XFire Client +http://xfire.codehaus.org)";
    public static final String HTTP_PROXY_HOST = "http.proxyHost";
    public static final String HTTP_PROXY_PORT = "http.proxyPort";
    public static final String HTTP_STATE = "httpClient.httpstate";
    public static final String HTTP_CLIENT = "httpClient";
    public static final String HTTP_TIMEOUT = "http.timeout";
    
    /** Enable GZIP on request and response. */
    public static final String GZIP_ENABLED = "gzip.enabled";
    
    /** Request GZIP encoded responses. */
    public static final String GZIP_RESPONSE_ENABLED = "gzip.response.enabled";
    
    /** GZIP the requests. */
    public static final String GZIP_REQUEST_ENABLED = "gzip.request.enabled";

    private InputStream msgIs;
    
    public CommonsHttpMessageSender(OutMessage message, MessageContext context)
    {
        super(message, context);
    }
    
    public void open()
        throws IOException, XFireException
    {
        MessageContext context = getMessageContext();

        createClient();
        
        // Pull the HttpState from the context if possible. Otherwise create
        // one in the ThreadLocal
        state = getHttpState();
        
        postMethod = new PostMethod(getUri());
        
        if (Boolean.valueOf((String) context.getContextualProperty(DISABLE_KEEP_ALIVE)).booleanValue()) {
            postMethod.setRequestHeader("Connection", "Close");
        }

        // set the username and password if present
        String username = (String) context.getContextualProperty(Channel.USERNAME);
        if (username != null)
        {
            String password = (String) context.getContextualProperty(Channel.PASSWORD);
            client.getParams().setAuthenticationPreemptive(true);
            
            int domainIndex = username.indexOf('\\');
            if (domainIndex > 0 && username.length() > domainIndex + 1) {

                state.setCredentials(
                        AuthScope.ANY, 
                        new NTCredentials(
                                username.substring(0, domainIndex), 
                                password, 
                                "localhost", // TODO: resolve local host name 
                                username.substring(domainIndex+1)));
                        
            } else {
                
                state.setCredentials( AuthScope.ANY, new UsernamePasswordCredentials(username,password));
                
            }
            
           // state.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        }
        
        if (getSoapAction() != null)
        {
            postMethod.setRequestHeader("SOAPAction", getQuotedSoapAction());
        }
        
        OutMessage message = getMessage();
        boolean mtomEnabled = Boolean.valueOf((String) context.getContextualProperty(SoapConstants.MTOM_ENABLED)).booleanValue();
        Attachments atts = message.getAttachments();
        
        if (mtomEnabled || atts != null)
        {
            if (atts == null)
            {
                atts = new JavaMailAttachments();
                message.setAttachments(atts);
            }
            
            OutMessageDataSource source = new OutMessageDataSource(context, message);
            DataHandler soapHandler = new DataHandler(source);
            atts.setSoapContentType(HttpChannel.getSoapMimeType(message, false));
            atts.setSoapMessage(new SimpleAttachment(source.getName(), soapHandler));
            
            postMethod.setRequestHeader("Content-Type", atts.getContentType());
        }
        else
        {
            postMethod.setRequestHeader("Content-Type", HttpChannel.getSoapMimeType(getMessage(), true));
        }
        
        if (isGzipResponseEnabled(context))
        {
            postMethod.setRequestHeader("Accept-Encoding", GZIP_CONTENT_ENCODING);
        }
        
        if (isGzipRequestEnabled(context))
        {
            postMethod.setRequestHeader("Content-Encoding", GZIP_CONTENT_ENCODING);
        }
    }

    private synchronized void createClient()
    {
        MessageContext context = getMessageContext();
        client = (HttpClient) ((HttpChannel) getMessage().getChannel()).getProperty(HTTP_CLIENT);
        if (client == null)
        {
            client = new HttpClient();
            client.setHttpConnectionManager(new MultiThreadedHttpConnectionManager());
            ((HttpChannel) getMessage().getChannel()).setProperty(HTTP_CLIENT, client);

            HttpClientParams params = (HttpClientParams) context.getContextualProperty(HTTP_CLIENT_PARAMS);
            if (params == null)
            {
                params = client.getParams();
                
                client.getParams().setParameter("http.useragent", USER_AGENT);
                boolean disableEC = Boolean.valueOf((String)context.getContextualProperty(DISABLE_EXPECT_CONTINUE)).booleanValue();
                client.getParams().setBooleanParameter("http.protocol.expect-continue", !disableEC);
                client.getParams().setVersion(HttpVersion.HTTP_1_1);
                String timeoutStr = (String) context.getContextualProperty(HTTP_TIMEOUT);
                if( timeoutStr != null){
                  client.getParams().setSoTimeout(Integer.parseInt(timeoutStr));
                }
            }
            else
            {
                client.setParams(params);
            }

            // Setup the proxy settings
            String proxyHost = (String) context.getContextualProperty(HTTP_PROXY_HOST);
            if (proxyHost == null)
            {
                proxyHost = System.getProperty(HTTP_PROXY_HOST);
            }
            
            if (proxyHost != null)
            { 
                String portS = (String) context.getContextualProperty(HTTP_PROXY_PORT);
                if (portS == null)
                {
                    portS = System.getProperty(HTTP_PROXY_PORT);
                }
                int port = 80;
                if (portS != null)
                    port = Integer.parseInt(portS);

                client.getHostConfiguration().setProxy(proxyHost, port);
            }
        }
    }

    static boolean isGzipRequestEnabled(MessageContext context)
    {
        if (isGzipEnabled(context)) return true;
        
        Object gzipReqEnabled = context.getContextualProperty(GZIP_REQUEST_ENABLED);
        return (gzipReqEnabled != null && gzipReqEnabled.toString().toLowerCase().equals("true"));
    }
    
    static boolean isGzipEnabled(MessageContext context)
    {
        Object gzipEnabled = context.getContextualProperty(GZIP_ENABLED);
        return (gzipEnabled != null && gzipEnabled.toString().toLowerCase().equals("true"));
    }
    
    static boolean isGzipResponseEnabled(MessageContext context)
    {
        if (isGzipEnabled(context)) return true;
        
        Object gzipResEnabled = context.getContextualProperty(GZIP_RESPONSE_ENABLED);
        return (gzipResEnabled != null && gzipResEnabled.toString().toLowerCase().equals("true"));
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
        
        client.executeMethod(null, postMethod, state);
    }

    public int getStatusCode(){
        return  postMethod.getStatusCode();
    }
    /**
     * @return
     */
    
    public boolean hasResponse()
    {
        NameValuePair pair = postMethod.getResponseHeader("Content-Type");
        if(pair == null) return false;
        
        String ct = pair.getValue();
        
        return ct != null && ct.length() > 0;
    }
    
    public HttpState getHttpState()
    {
        HttpState state = (HttpState) ((HttpChannel) getMessage().getChannel()).getProperty(HTTP_STATE);
        if (state == null) 
        {
            state = new HttpState();
            
            ((HttpChannel) getMessage().getChannel()).setProperty(HTTP_STATE, state);
        }
        
        return state;
    }

    private RequestEntity getByteArrayRequestEntity()
        throws IOException, XFireException
    {
        OutMessage message = getMessage();
        MessageContext context = getMessageContext();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStream os = bos;

        if (isGzipRequestEnabled(context))
        {
            os = new GZIPOutputStream(os);
        }
        
        Attachments atts = message.getAttachments();
        if (atts != null)
        {
            atts.write(os);
        }
        else
        {
            HttpChannel.writeWithoutAttachments(context, message, os);
        }
        
        os.close();
        
        return new ByteArrayRequestEntity(bos.toByteArray());
    }
    
    public InMessage getInMessage()
        throws IOException
    {
        String ct = postMethod.getResponseHeader("Content-Type").getValue();
        InputStream in = postMethod.getResponseBodyAsStream();
        Header hce = postMethod.getResponseHeader("Content-Encoding");
        
        if (hce != null && hce.getValue().equals(GZIP_CONTENT_ENCODING))
        {
            in = new GZIPInputStream(in);
        }
        
        if (ct.toLowerCase().indexOf("multipart/related") != -1)
        {
            Attachments atts = new StreamedAttachments(in, ct);

            msgIs = atts.getSoapMessage().getDataHandler().getInputStream();
            
            InMessage msg = new InMessage(STAXUtils.createXMLStreamReader(msgIs, getEncoding(),getMessageContext()), getUri());
            msg.setAttachments(atts);
            return msg;
        }
        else
        {
            return new InMessage(STAXUtils.createXMLStreamReader(in, getEncoding(),getMessageContext()), getUri());
        }
    }

    public PostMethod getMethod()
    {
        return this.postMethod;
    }

    public void close()
        throws XFireException
    {
        if (msgIs != null)
            try
            {
                msgIs.close();
            }
            catch (IOException e)
            {
                throw new XFireException("Could not close connection.", e);
            }
        
        if (postMethod != null)
            postMethod.releaseConnection();
    }
}
