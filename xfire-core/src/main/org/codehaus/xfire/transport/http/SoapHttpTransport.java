package org.codehaus.xfire.transport.http;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.FaultHandler;
import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.AbstractTransport;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.wsdl.WSDL11Transport;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SoapHttpTransport
    extends AbstractTransport
	implements Transport, WSDL11Transport
{
    public final static String NAME = "Http";

    public static final String HTTP_TRANSPORT_NS = "http://schemas.xmlsoap.org/soap/http";

    public SoapHttpTransport()
    {
        FaultHandlerPipeline faultPipe = new FaultHandlerPipeline();
        faultPipe.addHandler(new FaultResponseCodeHandler());
        setFaultPipeline(faultPipe);
    }
    
    /**
     * @see org.codehaus.xfire.transport.Transport#getName()
     */
    public String getName()
    {
        return NAME;
    }

    /**
	 * Get the URL for a particular service.
	 */
	public String getServiceURL( Service service )
	{
		HttpServletRequest req = XFireServletController.getRequest();
        
        return getWebappBase(req) + "/services/" + service.getName();
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
    
    public class FaultResponseCodeHandler
         implements FaultHandler
    {
        /**
         * @see org.codehaus.xfire.handler.Handler#invoke(org.codehaus.xfire.MessageContext)
         * @param context
         * @throws Exception
         */
        public void handleFault(XFireFault fault, MessageContext context)
        {
            XFireServletController.getResponse().setStatus(500);
        }    
    }
}
