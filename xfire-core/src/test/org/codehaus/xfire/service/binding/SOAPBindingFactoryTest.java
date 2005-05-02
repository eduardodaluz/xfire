package org.codehaus.xfire.service.binding;

import javax.xml.namespace.QName;

import junit.framework.TestCase;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapConstants;

public class SOAPBindingFactoryTest
        extends TestCase
{
    private QName name;

    protected void setUp()
            throws Exception
    {
        name = new QName("binding");
    }


    public void testCreateSOAPBindingDocumentDefaultVersion()
            throws Exception
    {
        SOAPBinding binding = SOAPBindingFactory.createSOAPBinding(name,
                                                                   SoapConstants.STYLE_DOCUMENT,
                                                                   SoapConstants.USE_LITERAL);
        assertNotNull(binding);
        assertDocumentBinding(binding);
        assertVersion11(binding);
    }

    public void testCreateSOAPBindingDocumentSpecificVersion()
            throws Exception
    {
        SOAPBinding binding = SOAPBindingFactory.createSOAPBinding(name,
                                                                   SoapConstants.STYLE_DOCUMENT,
                                                                   SoapConstants.USE_LITERAL,
                                                                   Soap12.getInstance());
        assertNotNull(binding);
        assertDocumentBinding(binding);
        assertVersion12(binding);
    }

    public void testCreateSOAPBindingWrappedDefaultVersion()
            throws Exception
    {
        SOAPBinding binding = SOAPBindingFactory.createSOAPBinding(name,
                                                                   SoapConstants.STYLE_WRAPPED,
                                                                   SoapConstants.USE_LITERAL);
        assertNotNull(binding);
        assertWrappedBinding(binding);
        assertVersion11(binding);
    }

    public void testCreateSOAPBindingWrappedSpecificVersion()
            throws Exception
    {
        SOAPBinding binding = SOAPBindingFactory.createSOAPBinding(name,
                                                                   SoapConstants.STYLE_WRAPPED,
                                                                   SoapConstants.USE_LITERAL,
                                                                   Soap12.getInstance());
        assertNotNull(binding);
        assertWrappedBinding(binding);
        assertVersion12(binding);
    }

    public void testCreateSOAPBindingRPCEncodedDefaultVersion()
            throws Exception
    {
        SOAPBinding binding = SOAPBindingFactory.createSOAPBinding(name,
                                                                   SoapConstants.STYLE_RPC,
                                                                   SoapConstants.USE_ENCODED);
        assertNotNull(binding);
        assertRPCEncodedBinding(binding);
        assertVersion11(binding);
    }

    public void testCreateSOAPBindingRPCEncodedSpecificVersion()
            throws Exception
    {
        SOAPBinding binding = SOAPBindingFactory.createSOAPBinding(name,
                                                                   SoapConstants.STYLE_RPC,
                                                                   SoapConstants.USE_ENCODED,
                                                                   Soap12.getInstance());
        assertNotNull(binding);
        assertRPCEncodedBinding(binding);
        assertVersion12(binding);
    }

    public void testCreateDocumentBindingDefaultVersion()
            throws Exception
    {
        SOAPBinding binding = SOAPBindingFactory.createDocumentBinding(name);
        assertDocumentBinding(binding);
        assertVersion11(binding);
    }

    public void testCreateDocumentBindingSpecificVersion()
            throws Exception
    {
        SOAPBinding binding = SOAPBindingFactory.createDocumentBinding(name, Soap12.getInstance());
        assertDocumentBinding(binding);
        assertVersion12(binding);
    }

    public void testCreateWrappedBindingDefaultVersion()
            throws Exception
    {
        SOAPBinding binding = SOAPBindingFactory.createWrappedBinding(name);
        assertWrappedBinding(binding);
        assertVersion11(binding);
    }

    public void testCreateWrappedBindingSpecificVersion()
            throws Exception
    {
        SOAPBinding binding = SOAPBindingFactory.createWrappedBinding(name, Soap12.getInstance());
        assertWrappedBinding(binding);
        assertVersion12(binding);
    }

    public void testCreateRPCEncodedBindingDefaultVersion()
            throws Exception
    {
        SOAPBinding binding = SOAPBindingFactory.createRPCEncodedBinding(name);
        assertRPCEncodedBinding(binding);
        assertVersion11(binding);
    }

    public void testCreateRPCEncodedBindingSpecificVersion()
            throws Exception
    {
        SOAPBinding binding = SOAPBindingFactory.createRPCEncodedBinding(name, Soap12.getInstance());
        assertRPCEncodedBinding(binding);
        assertVersion12(binding);
    }

    private void assertVersion11(SOAPBinding binding)
    {
        assertEquals(Soap11.getInstance(), binding.getSoapVersion());
    }

    private void assertVersion12(SOAPBinding binding)
    {
        assertEquals(Soap12.getInstance(), binding.getSoapVersion());
    }

    private void assertDocumentBinding(SOAPBinding binding)
    {
        assertEquals(SoapConstants.STYLE_DOCUMENT, binding.getStyle());
        assertEquals(SoapConstants.USE_LITERAL, binding.getUse());
    }

    private void assertWrappedBinding(SOAPBinding binding)
    {
        assertEquals(SoapConstants.STYLE_WRAPPED, binding.getStyle());
        assertEquals(SoapConstants.USE_LITERAL, binding.getUse());
    }

    private void assertRPCEncodedBinding(SOAPBinding binding)
    {
        assertEquals(SoapConstants.STYLE_RPC, binding.getStyle());
        assertEquals(SoapConstants.USE_ENCODED, binding.getUse());
    }

}