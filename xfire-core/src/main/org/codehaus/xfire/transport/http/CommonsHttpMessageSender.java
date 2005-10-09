package org.codehaus.xfire.transport.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.util.STAXUtils;

/**
 * Sends a http message via commons http client.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class CommonsHttpMessageSender extends AbstractMessageSender
{
    private PostMethod postMethod;

    private HttpClient client;

    public CommonsHttpMessageSender(OutMessage message, MessageContext context)
    {
        super(message, context);
    }
    
    public void open()
        throws IOException, XFireFault
    {
        client = new HttpClient();

        client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
        client.getParams()
                .setParameter("http.useragent", "XFire Client +http://xfire.codehaus.org");
        client.getParams().setParameter("http.protocol.content-charset", getEncoding());

        postMethod = new PostMethod(getUri());
        postMethod.setRequestHeader("Content-Type", "text/xml; charset="+getEncoding());
        if (getSoapAction() != null)
        {
            postMethod.setRequestHeader("SOAPAction", getSoapAction());
        }
    }

    public void send()
        throws HttpException, IOException
    {
        RequestEntity requestEntity = new OutMessageRequestEntity(getMessage(), getMessageContext());
        getMethod().setRequestEntity(requestEntity);
        
        client.executeMethod(postMethod);
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
