package org.codehaus.xfire.annotations.soap;


import junit.framework.TestCase;
import org.codehaus.xfire.soap.SoapConstants;

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

    public void testGetStyleString()
            throws Exception
    {
        soapBindingAnnotation.setParameterStyle(SOAPBindingAnnotation.PARAMETER_STYLE_WRAPPED);
        soapBindingAnnotation.setStyle(SOAPBindingAnnotation.STYLE_DOCUMENT);
        assertEquals(SoapConstants.STYLE_WRAPPED, soapBindingAnnotation.getStyleString());

        soapBindingAnnotation.setParameterStyle(SOAPBindingAnnotation.PARAMETER_STYLE_BARE);
        assertEquals(SoapConstants.STYLE_DOCUMENT, soapBindingAnnotation.getStyleString());

        soapBindingAnnotation.setStyle(SOAPBindingAnnotation.STYLE_RPC);
        assertEquals(SoapConstants.STYLE_RPC, soapBindingAnnotation.getStyleString());
    }


    public void testGetUseString()
            throws Exception
    {
        soapBindingAnnotation.setUse(SOAPBindingAnnotation.USE_ENCODED);
        assertEquals(SoapConstants.USE_ENCODED, soapBindingAnnotation.getUseString());

        soapBindingAnnotation.setUse(SOAPBindingAnnotation.USE_LITERAL);
        assertEquals(SoapConstants.USE_LITERAL, soapBindingAnnotation.getUseString());
    }
}