package org.codehaus.xfire.annotations;

import junit.framework.TestCase;

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
}