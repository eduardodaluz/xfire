package org.codehaus.xfire.attachments;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.Soap11FaultHandler;
import org.codehaus.xfire.handler.EchoHandler;
import org.codehaus.xfire.service.MessageService;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.yom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class AttachmentTest 
	extends AbstractXFireTest
{
    public void setUp() throws Exception
    {
        super.setUp();
        
        MessageService service = new MessageService();
        service.setName("Echo");
        service.setSoapVersion(Soap12.getInstance());
        service.setWSDLURL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl").toString());
        
        service.setServiceHandler(new EchoHandler());
        service.setFaultHandler(new Soap11FaultHandler());
        
        getServiceRegistry().register(service);
    }

	public void testAttachments()
		throws Exception
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
        
        Session session = Session.getDefaultInstance(new Properties(), null);
        MimeMessage inMsg = new MimeMessage(session, is);
        inMsg.addHeaderLine("Content-Type: " + sendAtts.getContentType());
        
        MimeMultipart inMP = (MimeMultipart) inMsg.getContent();

        JavaMailAttachments atts = new JavaMailAttachments(inMP);
        assertEquals(1, atts.size());
        assertNotNull(atts.getSoapMessage());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageContext context = 
            new MessageContext( "Echo",
                                null,
                                out,
                                null,
                                null );
        context.setProperty(JavaMailAttachments.ATTACHMENTS_KEY, atts);
        
        getXFire().invoke(atts.getSoapMessage().getDataHandler().getInputStream(), context);

        Document response = readDocument(out.toString());
        addNamespace("m", "urn:Echo");
        assertValid("//m:echo", response);
	}
    
    private DataHandler createDataHandler(String name) throws MessagingException
    {
        File f = new File(name);
        FileDataSource fs = new FileDataSource(f);
        
        return new DataHandler(fs);
    }
}
