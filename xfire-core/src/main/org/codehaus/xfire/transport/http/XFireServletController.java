package org.codehaus.xfire.transport.http;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.TransportManager;

/**
 * Loads XFire and processes requests.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 13, 2004
 */
public class XFireServletController
{
    private static ThreadLocal requests = new ThreadLocal();

    private File webInfPath;

    protected XFire xfire;

    protected ServletContext context;

    public XFireServletController(XFire xfire, ServletContext context)
    {
        this.xfire = xfire;
        this.context = context;

        registerTransport();
    }

    protected void registerTransport()
    {
        TransportManager service = getTransportManager();
        service.register(new SoapHttpTransport());
    }

    public static HttpServletRequest getRequest()
    {
        return (HttpServletRequest) requests.get();
    }

    public File getWebappBase()
    {
        if (webInfPath == null)
        {
            webInfPath = new File(context.getRealPath("/WEB-INF"));
        }

        return webInfPath;
    }

    /**
     * @return
     */
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
        String service = getService(request);
        ServiceRegistry reg = getServiceRegistry();

        String wsdl = request.getParameter("wsdl");
        response.setHeader("Content-Type", "UTF-8");

        requests.set(request);

        if (service == null || service.equals("") || !reg.hasService(service))
        {
            generateServices(response);
            return;
        }

        try
        {
            if (wsdl != null)
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
            throws IOException
    {
        response.setContentType("text/html");
        StringBuffer sb = new StringBuffer();
        sb.append("<html><head><title>XFire Services</title></head>").append(
                "<body>No such service.").append("<p>Services:<ul>.");

        ServiceRegistry registry = getServiceRegistry();
        Collection services = registry.getServices();

        for (Iterator itr = services.iterator(); itr.hasNext();)
        {
            Service service = (Service) itr.next();
            sb.append("<li>").append(service.getName()).append("</li>");
        }
        sb.append("</ul></p></body></html>");

        response.getWriter().write(sb.toString());
        response.getWriter().flush();
        // response.getWriter().close();

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
    protected void invoke(HttpServletRequest request,
                          HttpServletResponse response, 
                          String service)
        throws ServletException, IOException, UnsupportedEncodingException
    {
        response.setStatus(200);
        // response.setBufferSize(1024 * 8);
        response.setContentType("text/xml; charset=UTF-8");

        XFireHttpSession session = new XFireHttpSession(request);
        MessageContext context = new MessageContext(service, null, response
                .getOutputStream(), session, request.getRequestURI());

        getXFire().invoke(request.getInputStream(), context);
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
        // response.setBufferSize(1024 * 8);

        getXFire().generateWSDL(service, response.getOutputStream());
    }

    /**
     * @param request
     * @return
     */
    private String getService(HttpServletRequest request)
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

    /**
     * @return
     */
    public XFire getXFire()
    {
        return xfire;
    }

    public ServiceRegistry getServiceRegistry()
    {
        return xfire.getServiceRegistry();
    }

}
