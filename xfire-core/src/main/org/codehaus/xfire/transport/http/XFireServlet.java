package org.codehaus.xfire.transport.http;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.TransportManager;

/**
 * Loads XFire and processes requests via a servlet.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 13, 2004
 */
public class XFireServlet
    extends HttpServlet
{
    private static ThreadLocal requests = new ThreadLocal();
    
    private File webInfPath;
    
    private XFire xfire;
    
    public static HttpServletRequest getRequest()
    {
        return (HttpServletRequest) requests.get();
    }
    
    public File getWebappBase()
    {
        if ( webInfPath == null )
        {
            ServletContext context = getServletConfig().getServletContext();

            webInfPath = new File( context.getRealPath("/WEB-INF") );   
        }
        
    	return webInfPath;
    }
    
    public void init() throws ServletException
    {
        super.init();
        
        try
        {
            XFireFactory factory = XFireFactory.newInstance();
            
            xfire = factory.getXFire();
        }
        catch (Exception e)
        {
            throw new ServletException("Couldn't start XFire.", e);
        }
        
        TransportManager service = getTransportManager();
        
        String url = getInitParameter("url");
        if ( url == null )
        {
            url = "Please provide a url in your web.xml config.";
        }
        
        service.register( new SoapHttpTransport() );
    }
    
	/**
     * @return
	 * @throws ServletException
     */
    protected TransportManager getTransportManager()
    	throws ServletException
    {
        return xfire.getTransportManager();
    }

    /**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet( HttpServletRequest request, HttpServletResponse response )
			throws ServletException, IOException
	{
        String service = getService(request);
        ServiceRegistry reg = getServiceRegistry();
        
        String wsdl = request.getParameter("wsdl");
        response.setHeader("Content-Type", "UTF-8");
        
        requests.set(request);
        
        if ( service == null 
             || 
             service.equals("")
             || 
             !reg.hasService( service ) )
        {
            generateServices(response);
            return;
        }
        
        try
        {
            if ( wsdl != null )
            {
                generateWSDL(response, service);
            }
            else
            {
                invoke(request, response, service);
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	/**
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	private void generateServices(HttpServletResponse response) 
        throws IOException, ServletException
	{
		response.setContentType("text/html");
		StringBuffer sb = new StringBuffer();
        sb.append("<html><head><title>XFire Services</title></head>")
          .append("<body>No such service.")
          .append("<p>Services:<ul>.");
        
        ServiceRegistry registry = getServiceRegistry();
        Collection services = registry.getServices();
        
        for ( Iterator itr = services.iterator(); itr.hasNext(); )
        {
            Service service = (Service) itr.next();
            sb.append("<li>")
              .append(service.getName())
              .append("</li>");
        }
        sb.append("</ul></p></body></html>");
        
		response.getWriter().write( sb.toString() );
		response.getWriter().flush();
		//response.getWriter().close();
		  
		return;
	}

	/**
	 * @param request
	 * @param response
	 * @param service
	 * @throws ServletException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	protected void invoke(HttpServletRequest request, HttpServletResponse response, String service)
        throws ServletException, IOException, UnsupportedEncodingException
	{
        response.setStatus(200);
		//response.setBufferSize(1024 * 8);
        response.setContentType("text/xml; charset=UTF-8");
		
        XFireHttpSession session = new XFireHttpSession(request);
        MessageContext context = 
            new MessageContext( service,
                                null,
                                response.getOutputStream(),
                                session,
                                request.getRequestURI() );
        
        getXFire().invoke( request.getInputStream(),
                           context );
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
		//response.setBufferSize(1024 * 8);

		getXFire().generateWSDL( service, response.getOutputStream() );
	}

	/**
     * @param request
     * @return
     */
    private String getService(HttpServletRequest request)
    {
        String pathInfo = request.getPathInfo();
        
        if ( pathInfo == null )
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

    /**
	 * @return
	 */
	public XFire getXFire()
        throws ServletException
	{
		return xfire;
	}
    
    public ServiceRegistry getServiceRegistry() 
        throws ServletException
    {
        return xfire.getServiceRegistry();
    }
     
    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException,
        IOException
    {
        doGet(req, res);
    }
}
