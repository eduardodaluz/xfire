package org.codehaus.xfire.annotations.soap;


import junit.framework.TestCase;

public class SOAPBindingAnnotationTest
        extends TestCase
{
    private SOAPBindingAnnotation soapBindingAnnotation;

    protected void setUp()
            throws Exception
    {
        soapBindingAnnotation = new SOAPBindingAnnotation();
    }

    public void testSetStyle()
            throws Exception
    {
        try
        {
            soapBindingAnnotation.setStyle(999);
            fail("No IllegalArgumentException thrown");
        }
        catch (IllegalArgumentException e)
        {
            // expected behavior
        }
    }

    public void testSetUse()
            throws Exception
    {
        try
        {
            soapBindingAnnotation.setUse(999);
            fail("No IllegalArgumentException thrown");
        }
        catch (IllegalArgumentException e)
        {
            // expected behavior
        }
    }

    public void testSetParameterStyle()
            throws Exception
    {
        try
        {
            soapBindingAnnotation.setParameterStyle(999);
            fail("No IllegalArgumentException thrown");
        }
        catch (IllegalArgumentException e)
        {
            // expected behavior
        }
    }
}