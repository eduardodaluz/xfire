package org.codehaus.xfire.transport.http;

import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.servletunit.ServletRunner;

public class XFireConfigurableServletNewTest
    extends XFireConfigurableServletTest
{

    
    
    public void testServlet()
        throws Exception
    {
        // TODO Auto-generated method stub
        super.testServlet();
    }

    protected String getConfiguration()
    {
        return "/org/codehaus/xfire/transport/http/configurable-web-new.xml";
        
    }

    public void setUp()
        throws Exception
    {
        HttpUnitOptions.setExceptionsThrownOnErrorStatus(true);
        
        sr = new ServletRunner( getResourceAsStream(getConfiguration()) );
    }

}
