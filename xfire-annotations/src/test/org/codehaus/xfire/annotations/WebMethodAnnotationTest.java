package org.codehaus.xfire.annotations;


import junit.framework.TestCase;
import org.codehaus.xfire.service.OperationInfo;

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
        OperationInfo operationInfo = new OperationInfo("other");
        webMethodAnnotation.populate(operationInfo);
        assertEquals(webMethodAnnotation.getOperationName(), operationInfo.getName());
    }
}