/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.xfire.annotations.jsr181;

import java.lang.reflect.Method;

import org.codehaus.xfire.annotations.*;
import org.codehaus.xfire.test.AbstractXFireTypeTest;

public class Jsr181WebAnnotationsTest
        extends AbstractXFireTypeTest
{
    protected WebAnnotations webAnnotations;
    protected Class echoServiceClass;
    protected Class thisClass;
    protected Method echoMethod;
    protected Method dummyMethod;


    public void setUp()
            throws Exception
    {
        super.setUp();

        webAnnotations = new Jsr181WebAnnotations();
        echoServiceClass = Jsr181EchoService.class;
        thisClass = getClass();
        echoMethod = echoServiceClass.getMethod("echo", new Class[]{String.class});
        dummyMethod = thisClass.getMethod("dummy", new Class[]{String.class});
    }

    public void dummy(String s)
    {
        // required for negative testing
    }

    public void testHasWebServiceAnnotation()
            throws Exception
    {
        assertTrue("WebServiceAnnotation not set", webAnnotations.hasWebServiceAnnotation(echoServiceClass));
    }

    public void testHasNoWebServiceAnnotation()
            throws Exception
    {
        assertFalse("WebServiceAnnotation set", webAnnotations.hasWebServiceAnnotation(thisClass));
    }

    public void testHasWebMethodAnnotation()
            throws Exception
    {
        assertTrue("WebMethodAnnotation not set", webAnnotations.hasWebMethodAnnotation(echoMethod));
    }

    public void testHasNoWebMethodAnnotation()
            throws Exception
    {
        assertFalse("WebMethodAnnotation set", webAnnotations.hasWebMethodAnnotation(dummyMethod));
    }

    public void testHasWebResultAnnotation()
            throws Exception
    {
        assertTrue("WebResultAnnotation not set", webAnnotations.hasWebResultAnnotation(echoMethod));
    }

    public void testHasNoWebResultAnnotation()
            throws Exception
    {
        assertFalse("WebResultAnnotation set", webAnnotations.hasWebResultAnnotation(dummyMethod));
    }

    public void testHasWebParamAnnotation()
            throws Exception
    {
        assertTrue("WebParamAnnotation not set", webAnnotations.hasWebParamAnnotation(echoMethod, 0));
    }

    public void testHasNoWebParamAnnotation()
            throws Exception
    {
        assertFalse("WebParamAnnotation set", webAnnotations.hasWebParamAnnotation(dummyMethod, 0));
    }

    public void testGetWebServiceAnnotation()
            throws Exception
    {
        WebServiceAnnotation webService = webAnnotations.getWebServiceAnnotation(echoServiceClass);
        assertNotNull(webService);
        assertEquals("EchoService", webService.getName());
        assertEquals("http://www.openuri.org/2004/04/HelloWorld", webService.getTargetNamespace());
    }

    public void testGetNullWebServiceAnnotation()
            throws Exception
    {
        WebServiceAnnotation webService = webAnnotations.getWebServiceAnnotation(thisClass);
        assertNull(webService);
    }

    public void testGetWebMethodAnnotation()
            throws Exception
    {
        WebMethodAnnotation webMethod = webAnnotations.getWebMethodAnnotation(echoMethod);
        assertNotNull(webMethod);
        assertEquals("Invalid operation name", "echoString", webMethod.getOperationName());
        assertEquals("Invalid action", "urn:EchoString", webMethod.getAction());
    }

    public void testGetNullWebMethodAnnotation()
            throws Exception
    {
        WebMethodAnnotation webMethod = webAnnotations.getWebMethodAnnotation(dummyMethod);
        assertNull(webMethod);
    }

    public void testGetWebResultAnnotation()
            throws Exception
    {
        WebResultAnnotation webResult = webAnnotations.getWebResultAnnotation(echoMethod);
        assertNotNull(webResult);
        assertEquals("echoResult", webResult.getName());
    }

    public void testGetNullWebResultAnnotation()
            throws Exception
    {
        WebResultAnnotation webResult = webAnnotations.getWebResultAnnotation(dummyMethod);
        assertNull(webResult);
    }

    public void testGetWebParamAnnotation()
            throws Exception
    {
        WebParamAnnotation webParam = webAnnotations.getWebParamAnnotation(echoMethod, 0);
        assertNotNull(webParam);
        assertEquals("echoParam", webParam.getName());
    }

    public void testGetNullWebParamAnnotation()
            throws Exception
    {
        WebParamAnnotation webParam = webAnnotations.getWebParamAnnotation(dummyMethod, 0);
        assertNull(webParam);
    }
}
