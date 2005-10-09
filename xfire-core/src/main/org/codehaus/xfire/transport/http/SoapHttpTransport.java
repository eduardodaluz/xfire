package org.codehaus.xfire.transport.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.AbstractWSDLTransport;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.DefaultEndpoint;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SoapHttpTransport
    extends AbstractWSDLTransport
{
    private static final Log log = LogFactory.getLog(SoapHttpTransport.class);
    
    public final static String NAME = "Http";

    public final static String HTTP_TRANSPORT_NS = "http://schemas.xmlsoap.org/soap/http";

    private final static String URI_PREFIX = "urn:xfire:transport:http:";

    public SoapHttpTransport()
    {
        addFaultHandler(new FaultResponseCodeHandler());
    }
    
    /**
     * @see org.codehaus.xfire.transport.Transport#getName()
     */
    public String getName()
    {
        return NAME;
    }

    protected Channel createNewChannel(String uri)
    {
        log.debug("Creating new channel for uri: " + uri);
        
        HttpChannel c = new HttpChannel(uri, this);
        c.setEndpoint(new DefaultEndpoint());

        return c;
    }

    protected String getUriPrefix()
    {
        return URI_PREFIX;
    }
    /**
	 * Get the URL for a particular service.
	 */
	public String getServiceURL( Service service )
	{
		HttpServletRequest req = XFireServletController.getRequest();
        
        if (req == null)
        {
            return "http://localhost/services/" + service.getName();
        }
        
        StringBuffer output = new StringBuffer( 128 );

        output.append( req.getScheme() );
        output.append( "://" );
        output.append( req.getServerName() );

        if ( req.getServerPort() != 80 &&
             req.getServerPort() != 443 &&
             req.getServerPort() != 0 )
        {
            output.append( ':' );
            output.append( req.getServerPort() );
        }

        output.append( req.getRequestURI() );

        return output.toString();
	}

    public String getTransportURI( Service service )
    {
        return HTTP_TRANSPORT_NS;
    }
    
    protected String getWebappBase(HttpServletRequest request)
	{
		StringBuffer baseURL = new StringBuffer(128);
		baseURL.append(request.getScheme());
		baseURL.append("://");
		baseURL.append(request.getServerName());
		if (request.getServerPort() != 80)
		{
			baseURL.append(":");
			baseURL.append(request.getServerPort());
		}
		baseURL.append(request.getContextPath());
		return baseURL.toString();
	}

    public String[] getKnownUriSchemes()
    {
        return new String[] { "http://", "https://" };
    }
    
    public class FaultResponseCodeHandler
         extends AbstractHandler
     {        
        public String getPhase()
        {
            return Phase.TRANSPORT;
        }

        /**
         * @see org.codehaus.xfire.handler.Handler#invoke(org.codehaus.xfire.MessageContext)
         * @param context
         * @throws Exception
         */
        public void invoke(MessageContext context)
        {
            HttpServletResponse response = XFireServletController.getResponse();
            if ( response != null )
                response.setStatus(500);
        }    
    }
}
