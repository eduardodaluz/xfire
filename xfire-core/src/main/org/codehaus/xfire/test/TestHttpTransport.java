package org.codehaus.xfire.test;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.http.SoapHttpTransport;

/**
 * TestHttpTransport
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class TestHttpTransport
    extends SoapHttpTransport
{
    public String getServiceURL( Service service )
    {
        return "http://localhost/services/" + service.getName();
	}
}
