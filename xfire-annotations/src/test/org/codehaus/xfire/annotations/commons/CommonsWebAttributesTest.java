package org.codehaus.xfire.annotations.commons;

import java.lang.reflect.Method;

import junit.framework.TestCase;
import org.codehaus.xfire.annotations.WebMethodAnnotation;
import org.codehaus.xfire.annotations.WebServiceAnnotation;

public class CommonsWebAttributesTest
        extends TestCase
{
    private CommonsWebAttributes webAttributes;
    private Method doItMethod;

    protected void setUp()
            throws Exception
    {
        webAttributes = new CommonsWebAttributes();
        doItMethod = TestingService.class.getMethod("doIt", new Class[0]);
    }

    public void testHasWebServiceAnnotation()
            throws Exception
    {
        assertTrue("Attribute not set", webAttributes.hasWebServiceAnnotation(TestingService.class));
    }


    public void testHasWebMethodAnnotation()
            throws Exception
    {
        assertTrue("Attribute not set", webAttributes.hasWebMethodAnnotation(doItMethod));
    }

    public void testGetWebServiceAnnotation()
            throws Exception
    {
        WebServiceAnnotation webService = webAttributes.getWebServiceAnnotation(TestingService.class);
        assertNotNull(webService);
        assertEquals("name", webService.getName());
    }

    public void testGetWebMethodAnnotation()
            throws Exception
    {
        WebMethodAnnotation webMethod = webAttributes.getWebMethodAnnotation(doItMethod);
        assertNotNull(webMethod);
        assertEquals("action", webMethod.getAction());
    }
}