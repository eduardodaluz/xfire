package org.codehaus.xfire.spring;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.test.AbstractServletTest;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SpringServletTest
    extends AbstractServletTest
{
    BeanFactory factory;
    
    public void setUp()
        throws Exception
    {
        ClassPathResource res = new ClassPathResource("/org/codehaus/xfire/spring/echoBeans.xml");
        factory = new XmlBeanFactory(res);
        
        super.setUp();
    }

    public void testServlet() throws Exception
    {
        WebResponse response = newClient().getResponse( "http://localhost/Echo?wsdl" );
        
        System.out.println(response.getText());
        
        WebRequest req = new PostMethodWebRequest( "http://localhost/Echo",
                getClass().getResourceAsStream("/org/codehaus/xfire/spring/echoRequest.xml"),
                "text/xml" );

        response = newClient().getResponse(req);
    }
    
    protected String getConfiguration()
    {
        return "/org/codehaus/xfire/spring/web.xml";
    }
    
    protected XFire getXFire()
    {
        return (XFire) factory.getBean(BeanConstants.XFIRE);
    }

}
