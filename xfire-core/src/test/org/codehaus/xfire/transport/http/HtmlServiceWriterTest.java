package org.codehaus.xfire.transport.http;

/**
 * @author Arjen Poutsma
 */

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.codehaus.xfire.service.Service;
import org.custommonkey.xmlunit.XMLTestCase;
import org.easymock.MockControl;

public class HtmlServiceWriterTest
        extends XMLTestCase
{
    private HtmlServiceWriter htmlServiceWriter;
    private MockControl serviceMockControl;
    private Service service;

    protected void setUp()
            throws Exception
    {
        htmlServiceWriter = new HtmlServiceWriter();
        serviceMockControl = MockControl.createControl(Service.class);
        service = (Service) serviceMockControl.getMock();
    }

    public void testdescribeServices()
            throws Exception
    {
        service.getName();
        serviceMockControl.setReturnValue("service");

        serviceMockControl.replay();
        ArrayList services = new ArrayList();
        services.add(service);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        htmlServiceWriter.write(os, services);
        os.close();

        String expected = "<?xml version='1.0' encoding='utf-8'?>" + "" +
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" " +
                "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
                "<html><head><title>XFire Services</title></head><body>" +
                "<p>No such service</p><p>Services:</p><ul><li>service</li></ul></body></html>";
        assertXMLEqual(expected, os.toString());

        serviceMockControl.verify();
    }

    public void testDescribeService()
            throws Exception
    {
        service.getName();
        serviceMockControl.setReturnValue("service");

        serviceMockControl.replay();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        htmlServiceWriter.write(os, service);
        os.close();

        String expected = "<?xml version='1.0' encoding='utf-8'?>" + "" +
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" " +
                "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
                "<html><head><title>service Web Service</title></head><body>" +
                "<h1>service Web Service</h1></body></html>";
        assertXMLEqual(expected, os.toString());

        serviceMockControl.verify();

    }
}