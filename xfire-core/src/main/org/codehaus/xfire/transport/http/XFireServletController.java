package org.codehaus.xfire.transport.http;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.attachments.JavaMailAttachments;
import org.codehaus.xfire.exchange.AbstractMessage;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.util.STAXUtils;

/**
 * Loads XFire and processes requests.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 13, 2004
 */
public class XFireServletController
{
    
    private static ThreadLocal requests = new ThreadLocal();
    private static ThreadLocal responses = new ThreadLocal();
    private final static Log logger = LogFactory.getLog(XFireServletController.class);
    
    protected XFire xfire;

    protected SoapHttpTransport transport;

    private final static MimeTypeHandler mimeHandler = new MimeTypeHandler();
    
    public XFireServletController(XFire xfire)
    {
        this.xfire = xfire;
        
        // Create a SOAP Http transport with all the servlet addons
        transport = new SoapHttpTransport() 
        {
            public String getServiceURL(Service service)
            {
                HttpServletRequest req = XFireServletController.getRequest();

                if (req == null) return super.getServiceURL(service);
                
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
        };
        
        transport.addFaultHandler(new FaultResponseCodeHandler());
        transport.addFaultHandler(mimeHandler);
        transport.addOutHandler(mimeHandler);
        
        Transport oldSoap = getTransportManager().getTransport(SoapHttpTransport.SOAP11_HTTP_BINDING);
        
        if (oldSoap != null) getTransportManager().unregister(oldSoap);
        
        getTransportManager().register(transport);
    }

    public static HttpServletRequest getRequest()
    {
        return (HttpServletRequest) requests.get();
    }

    public static HttpServletResponse getResponse()
    {
        return (HttpServletResponse) responses.get();
    }

    protected TransportManager getTransportManager()
    {
        return getXFire().getTransportManager();
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
            *      javax.servlet.http.HttpServletResponse)
     */
    public void doService(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        String serviceName = getService(request);
        ServiceRegistry reg = getServiceRegistry();

        response.setHeader("Content-Type", "UTF-8");

        requests.set(request);
        responses.set(response);

        if (serviceName == null || 
                serviceName.length() == 0 || 
                !reg.hasService(serviceName))
        {
            if (!reg.hasService(serviceName))
            {
                response.setStatus(404);
            }

            generateServices(response);
            return;
        }

        try
        {
            if (request.getQueryString() != null &&
                request.getQueryString().trim().equalsIgnoreCase("wsdl"))
            {
                generateWSDL(response, serviceName);
            }
            else
            {
                invoke(request, response, serviceName);
            }
        }
        catch (Exception e)
        {
            logger.error("Couldn't invoke servlet request.", e);
            
            if (e instanceof ServletException)
            {
                throw (ServletException) e;
            }
            else
            {
                throw new ServletException(e);
            }
        }
        finally
        {
            requests.set(null);
            responses.set(null);
        }
    }

    protected void generateService(HttpServletResponse response, String serviceName)
            throws ServletException, IOException
    {
        response.setContentType("text/html");
        Service endpoint = getServiceRegistry().getService(serviceName);
        HtmlServiceWriter writer = new HtmlServiceWriter();
        try
        {
            writer.write(response.getOutputStream(), endpoint);
        }
        catch (XMLStreamException e)
        {
            throw new ServletException("Error writing HTML services list", e);
        }
    }


    /**
     * @param response
     */
    protected void generateServices(HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("text/html");

        HtmlServiceWriter writer = new HtmlServiceWriter();
        try
        {
            writer.write(response.getOutputStream(), getServiceRegistry().getServices());
        }
        catch (XMLStreamException e)
        {
            throw new ServletException("Error writing HTML services list", e);
        }
    }

    /**
     * @param request
     * @param response
     * @param service
     * @throws ServletException
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    protected void invoke(HttpServletRequest request,
                          HttpServletResponse response,
                          String service)
            throws ServletException, IOException, UnsupportedEncodingException
    {
        // TODO: Return 500 on a fault
        // TODO: Determine if the request is a soap request
        // and if not responsed appropriately
        
        response.setStatus(200);
        // response.setBufferSize(1024 * 8);

        XFireHttpSession session = new XFireHttpSession(request);
        MessageContext context = new MessageContext();
        context.setXFire(getXFire());
        context.setSession(session);
        context.setService(getService(service));
        
        Channel channel;
        try
        {
            channel = transport.createChannel(request.getRequestURI());
        }
        catch (Exception e)
        {
            logger.debug("Couldn't open channel.", e);
            throw new ServletException("Couldn't open channel.", e);
        }
        
        String contentType = request.getContentType();
        if (null == contentType)
        {
            response.setContentType("text/html; charset=UTF-8");
            // TODO: generate service description here
            
            response.getWriter().write("<html><body>Invalid SOAP request.</body></html>");
            response.getWriter().close();
        }
        else if (contentType.toLowerCase().indexOf("multipart/related") != -1)
        {
            try
            {
                Attachments atts = new JavaMailAttachments(request.getInputStream(), 
                                                           request.getContentType());
                
                XMLStreamReader reader = 
                    STAXUtils.createXMLStreamReader(atts.getSoapMessage().getDataHandler().getInputStream(), 
                                                    request.getCharacterEncoding());
                InMessage message = new InMessage(reader, request.getRequestURI());
                message.setProperty(SoapConstants.SOAP_ACTION, 
                                    request.getHeader(SoapConstants.SOAP_ACTION));
                message.setAttachments(atts);
                
                channel.receive(context, message);
            }
            catch (MessagingException e)
            {
                throw new XFireRuntimeException("Couldn't parse request message.", e);
            }
        }
        else
        {
            XMLStreamReader reader = 
                STAXUtils.createXMLStreamReader(request.getInputStream(), 
                                                request.getCharacterEncoding());
            
            InMessage message = new InMessage(reader, request.getRequestURI());
            channel.receive(context, message);
        }
    }

    /**
     * @param response
     * @param service
     * @throws ServletException
     * @throws IOException
     */
    protected void generateWSDL(HttpServletResponse response, String service)
            throws ServletException, IOException
    {
        response.setStatus(200);
        response.setContentType("text/xml");

        getXFire().generateWSDL(service, response.getOutputStream());
    }

    /**
     * Get the service that is mapped to the specified request.
     */
    protected String getService(HttpServletRequest request)
    {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null)
            return null;

        String serviceName;

        if (pathInfo.startsWith("/"))
        {
            serviceName = pathInfo.substring(1);
        }
        else
        {
            serviceName = pathInfo;
        }

        return serviceName;
    }

    protected Service getService(String name)
    {
        return getXFire().getServiceRegistry().getService(name);
    }

    public XFire getXFire()
    {
        return xfire;
    }

    public ServiceRegistry getServiceRegistry()
    {
        return xfire.getServiceRegistry();
    }
    
    public static class FaultResponseCodeHandler
        extends AbstractHandler
    {        
       public String getPhase()
       {
           return Phase.TRANSPORT;
       }
    
       /**
        * @see org.codehaus.xfire.handler.Handler#invoke(org.codehaus.xfire.MessageContext)
        * @param context
        */
       public void invoke(MessageContext context)
       {
           HttpServletResponse response = XFireServletController.getResponse();
           if ( response != null )
               response.setStatus(500);
       }    
    }

    public static class MimeTypeHandler
        extends AbstractHandler
    {
        public String getPhase()
        {
            return Phase.TRANSPORT;
        }

        /**
         * @see org.codehaus.xfire.handler.Handler#invoke(org.codehaus.xfire.MessageContext)
         * @param context
         */
        public void invoke(MessageContext context)
        {
            HttpServletResponse response = XFireServletController.getResponse();
            if (response != null)
            {
                AbstractMessage msg = context.getCurrentMessage();

                //response.setContentType(HttpChannel.getMimeType(msg));    
            }
        }
    }
}
