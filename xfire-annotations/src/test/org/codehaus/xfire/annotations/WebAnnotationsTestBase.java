package org.codehaus.xfire.annotations;

import java.lang.reflect.Method;

import org.codehaus.xfire.test.AbstractXFireTypeTest;

/**
 * Base class for unit tests that determine annotations.
 *
 * @author Arjen Poutsma
 */
public abstract class WebAnnotationsTestBase
        extends AbstractXFireTypeTest
{
    protected WebAnnotations webAnnotations;
    protected Class echoServiceClass;
    protected Method echoMethod;

    public void setUp()
            throws Exception
    {
        super.setUp();
        
        webAnnotations = getWebAnnotations();
        echoServiceClass = getEchoServiceClass();
        echoMethod = echoServiceClass.getMethod("echo", new Class[]{String.class});
    }

    protected abstract WebAnnotations getWebAnnotations();

    protected abstract Class getEchoServiceClass();

    public void testHasWebServiceAnnotation()
            throws Exception
    {
        assertTrue("WebServiceAnnotation not set", webAnnotations.hasWebServiceAnnotation(echoServiceClass));
    }

    public void testHasWebMethodAnnotation()
            throws Exception
    {
        assertTrue("WebMethodAnnotation not set", webAnnotations.hasWebMethodAnnotation(echoMethod));
    }

    public void testHasWebResultAnnotation()
            throws Exception
    {
        assertTrue("WebResultAnnotation not set", webAnnotations.hasWebResultAnnotation(echoMethod));
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

    public void testGetWebResultAnnotation()
            throws Exception
    {
        WebResultAnnotation webResult = webAnnotations.getWebResultAnnotation(echoMethod);
        assertNotNull(webResult);
        assertEquals("echoResult", webResult.getName());
    }

}
