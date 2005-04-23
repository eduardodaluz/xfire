package org.codehaus.xfire.annotations;


import junit.framework.TestCase;
import org.codehaus.xfire.service.ServiceInfo;

public class WebServiceAnnotationTest
        extends TestCase
{
    private WebServiceAnnotation webServiceAnnotation;

    protected void setUp()
            throws Exception
    {
        webServiceAnnotation = new WebServiceAnnotation();
    }


    public void testPopulate()
            throws Exception
    {
        ServiceInfo serviceInfo = new ServiceInfo();
        webServiceAnnotation.setName("name");
        webServiceAnnotation.setTargetNamespace("namespace");

        webServiceAnnotation.populate(serviceInfo);
        assertEquals(webServiceAnnotation.getName(), serviceInfo.getName());
        assertEquals(webServiceAnnotation.getTargetNamespace(), serviceInfo.getNamespace());
    }
}