package org.codehaus.xfire.annotations;

import java.lang.reflect.Method;

import junit.framework.TestCase;

/**
 * @author Arjen Poutsma
 */
public abstract class WebAnnotationsTestBase
        extends TestCase
{
    protected WebAnnotations webAnnotations;
    protected Class echoServiceClass;
    protected Method echoMethod;

    protected void setUp()
            throws Exception
    {
        webAnnotations = getWebAnnotations();
        echoServiceClass = getEchoServiceClass();
        echoMethod = echoServiceClass.getMethod("echo", new Class[]{String.class});
    }

    protected abstract WebAnnotations getWebAnnotations();

    protected abstract Class getEchoServiceClass();

    public void testHasWebServiceAnnotation()
            throws Exception
    {
        assertTrue("Attribute not set", webAnnotations.hasWebServiceAnnotation(echoServiceClass));
    }

    public void testHasWebMethodAnnotation()
            throws Exception
    {
        assertTrue("Attribute not set", webAnnotations.hasWebMethodAnnotation(echoMethod));
    }

    public void testGetWebServiceAnnotation()
            throws Exception
    {
        WebServiceAnnotation webService = webAnnotations.getWebServiceAnnotation(echoServiceClass);
        assertNotNull(webService);
        assertEquals("EchoService", webService.getName());
        assertEquals("http://www.openuri.org/2004/04/HelloWorld", webService.getTargetNamespace());
    }

    public void testGetWebMethodAnnotation()
            throws Exception
    {
        WebMethodAnnotation webMethod = webAnnotations.getWebMethodAnnotation(echoMethod);
        assertNotNull(webMethod);
        assertEquals("echoString", webMethod.getOperationName());
        assertEquals("urn:EchoString", webMethod.getAction());
    }
}
