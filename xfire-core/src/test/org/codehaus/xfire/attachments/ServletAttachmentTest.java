package org.codehaus.xfire.attachments;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;

import org.codehaus.xfire.fault.Soap12FaultHandler;
import org.codehaus.xfire.handler.EchoHandler;
import org.codehaus.xfire.service.SimpleService;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.transport.http.AbstractServletTest;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * XFireServletTest
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class ServletAttachmentTest
    extends AbstractServletTest
{
    public void setUp() throws Exception
    {
        super.setUp();
        
        SimpleService service = new SimpleService();
        service.setName("Echo");
        service.setSoapVersion(Soap12.getInstance());
        service.setWSDLURL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl").toString());
        
        service.setServiceHandler(new EchoHandler());
        service.setFaultHandler(new Soap12FaultHandler());
        
        getServiceRegistry().register(service);
    }
    
    public void testServlet() throws Exception
    {
        WebRequest req = getRequestMessage();

        WebResponse response = newClient().getResponse(req);

        System.out.println(response.getText());
    }
    
    public WebRequest getRequestMessage() throws Exception
    {
        JavaMailAttachments sendAtts = new JavaMailAttachments();
	    
	    sendAtts.setSoapMessage(
	        new SimpleAttachment("echo.xml", 
	            createDataHandler("./src/test/org/codehaus/xfire/attachments/echo11.xml")));
	    
	    sendAtts.addPart(
	        new SimpleAttachment("xfire_logo.jpg", 
	            createDataHandler("./src/test/org/codehaus/xfire/attachments/xfire_logo.jpg")));

	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
        sendAtts.write(bos);
        
        InputStream is = new ByteArrayInputStream(bos.toByteArray());
        
        PostMethodWebRequest req = new PostMethodWebRequest( 
            "http://localhost/services/Echo", is, sendAtts.getContentType() );
        
        return req;
    }

    private DataHandler createDataHandler(String name) 
    	throws MessagingException
    {
        File f = new File(name);
        FileDataSource fs = new FileDataSource(f);
        
        return new DataHandler(fs);
    }
}
