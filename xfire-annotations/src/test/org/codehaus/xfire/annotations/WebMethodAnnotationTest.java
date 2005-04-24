package org.codehaus.xfire.annotations;


import junit.framework.TestCase;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceInfo;

public class WebMethodAnnotationTest
        extends TestCase
{
    private WebMethodAnnotation webMethodAnnotation;

    protected void setUp()
            throws Exception
    {
        webMethodAnnotation = new WebMethodAnnotation();
    }


    public void testPopulate()
            throws Exception
    {
        webMethodAnnotation.setOperationName("name");
        OperationInfo operationInfo = new ServiceInfo().addOperation("other");
        webMethodAnnotation.populate(operationInfo);
        assertEquals(webMethodAnnotation.getOperationName(), operationInfo.getName());
    }
}