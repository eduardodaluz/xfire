package org.codehaus.xfire.annotations;


import junit.framework.TestCase;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.ServiceInfo;

public class WebResultAnnotationTest
        extends TestCase
{
    private WebResultAnnotation webResultAnnotation;

    protected void setUp()
            throws Exception
    {
        webResultAnnotation = new WebResultAnnotation();
    }


    public void testPopulate()
            throws Exception
    {
        webResultAnnotation.setName("name");
        webResultAnnotation.setTargetNamespace("namespace");
        MessagePartInfo partInfo = new ServiceInfo().addOperation("operation").createMessage("name").addMessagePart(
                "other1");
        partInfo.setNamespace("other2");
        webResultAnnotation.populate(partInfo);
        assertEquals(webResultAnnotation.getName(), partInfo.getName());
        assertEquals(webResultAnnotation.getTargetNamespace(), partInfo.getNamespace());
    }
}