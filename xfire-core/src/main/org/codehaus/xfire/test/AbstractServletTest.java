package org.codehaus.xfire.test;

import java.io.IOException;
import java.net.MalformedURLException;

import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.xml.sax.SAXException;

/**
 * A generic test-case for testing servlets.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 4, 2003
 */
public abstract class AbstractServletTest
    extends AbstractXFireTest
{
    private ServletRunner sr;
    
    private XFireFactory factory;
    
    private XFire xfire;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        factory = XFireFactory.newInstance();
        xfire = factory.getXFire();
        
        HttpUnitOptions.setExceptionsThrownOnErrorStatus(true);
    
        sr = new ServletRunner( getResourceAsStream(getConfiguration()) );
    }

    /**
     * @return The web.xml to use for testing.
     */
    protected String getConfiguration()
    {
        return "/org/codehaus/xfire/transport/http/web.xml";
    }

    protected XFire getXFire()
    {
        return xfire;
    }
    
    protected ServletUnitClient newClient()
    {
        ServletUnitClient client = sr.newClient();

        return sr.newClient();
    }
    
    /**
     * Here we expect an errorCode other than 200, and look for it
     * checking for text is omitted as it doesnt work. It would never work on
     * java1.3, but one may have expected java1.4+ to have access to the
     * error stream in responses. Clearly not.
     * @param request
     * @param errorCode
     * @param errorText optional text string to search for
     * @throws MalformedURLException
     * @throws IOException
     * @throws SAXException
     */
    protected void expectErrorCode(
        WebRequest request,
        int errorCode,
        String errorText)
        throws MalformedURLException, IOException, SAXException
    {
        String failureText =
            "Expected error " + errorCode + " from " + request.getURL();
    
        try
        {
            WebResponse response = newClient().getResponse(request);
            fail(errorText + " -got success instead");
        }
        catch (HttpException e)
        {
            assertEquals(failureText, errorCode, e.getResponseCode());
            /* checking for text omitted as it doesnt work.
            if(errorText!=null) {
            	assertTrue(
            			"Failed to find "+errorText+" in "+ e.getResponseMessage(),
            			e.getMessage().indexOf(errorText)>=0);
            }
            */
        }
    }

}