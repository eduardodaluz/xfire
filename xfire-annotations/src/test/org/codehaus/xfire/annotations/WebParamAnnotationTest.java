package org.codehaus.xfire.annotations;

import junit.framework.TestCase;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.ServiceInfo;

public class WebParamAnnotationTest
        extends TestCase
{
    private WebParamAnnotation webParamAnnotation;

    protected void setUp()
            throws Exception
    {
        webParamAnnotation = new WebParamAnnotation();
    }

    public void testSetMode()
            throws Exception
    {
        try
        {
            webParamAnnotation.setMode(999);
            fail("No IllegalArgumentException thrown");
        }
        catch (IllegalArgumentException e)
        {
            // expected behavior
        }
    }

    public void testPopulate()
            throws Exception
    {
        webParamAnnotation.setName("name");
        webParamAnnotation.setTargetNamespace("namespace");

        MessagePartInfo partInfo = new ServiceInfo().addOperation("operation").createMessage("name").addMessagePart(
                "other1");
        partInfo.setNamespace("other2");
        webParamAnnotation.populate(partInfo);

        assertEquals(webParamAnnotation.getName(), partInfo.getName());
        assertEquals(webParamAnnotation.getTargetNamespace(), partInfo.getNamespace());
    }
}