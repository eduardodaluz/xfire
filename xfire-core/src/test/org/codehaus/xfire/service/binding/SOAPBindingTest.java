package org.codehaus.xfire.service.binding;

import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPFault;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

public class SOAPBindingTest
        extends TestCase
{
    private SOAPBinding soapBinding;
    private QName name;

    protected void setUp()
            throws Exception
    {
        name = new QName("SoapBinding");
        soapBinding = new SOAPBinding(name);
    }


    public void testPopulateWSDLBinding()
            throws Exception
    {
        WSDLFactory factory = WSDLFactory.newInstance();
        Definition definition = factory.newDefinition();
        javax.wsdl.Binding wsdlBinding = definition.createBinding();

        soapBinding.populateWSDLBinding(definition, wsdlBinding);
        assertFalse(wsdlBinding.getExtensibilityElements().isEmpty());
        javax.wsdl.extensions.soap.SOAPBinding wsdlSoapBinding =
                (javax.wsdl.extensions.soap.SOAPBinding) wsdlBinding.getExtensibilityElements().get(0);
        assertNotNull(wsdlSoapBinding);
        assertEquals(new QName(SOAPBinding.WSDL_SOAP_NAMESPACE, "binding"), wsdlSoapBinding.getElementType());
        assertEquals(soapBinding.getStyle(), wsdlSoapBinding.getStyle());
    }

    public void testPopulateWSDLBindingOperation()
            throws Exception
    {
        WSDLFactory factory = WSDLFactory.newInstance();
        Definition definition = factory.newDefinition();
        BindingOperation bindingOperation = definition.createBindingOperation();

        soapBinding.populateWSDLBindingOperation(definition, bindingOperation);
        assertFalse(bindingOperation.getExtensibilityElements().isEmpty());
        SOAPOperation soapOperation =
                (SOAPOperation) bindingOperation.getExtensibilityElements().get(0);
        assertNotNull(soapOperation);
        assertEquals(new QName(SOAPBinding.WSDL_SOAP_NAMESPACE, "operation"), soapOperation.getElementType());
    }

    public void testPopulateWSDLBindingInput()
            throws Exception
    {
        WSDLFactory factory = WSDLFactory.newInstance();
        Definition definition = factory.newDefinition();
        BindingInput bindingInput = definition.createBindingInput();

        soapBinding.populateWSDLBindingInput(definition, bindingInput);
        assertFalse(bindingInput.getExtensibilityElements().isEmpty());
        SOAPBody soapBody =
                (SOAPBody) bindingInput.getExtensibilityElements().get(0);
        assertNotNull(soapBody);
        assertEquals(new QName(SOAPBinding.WSDL_SOAP_NAMESPACE, "body"), soapBody.getElementType());
    }

    public void testPopulateWSDLBindingOutput()
            throws Exception
    {
        WSDLFactory factory = WSDLFactory.newInstance();
        Definition definition = factory.newDefinition();
        BindingOutput bindingOutput = definition.createBindingOutput();

        soapBinding.populateWSDLBindingOutput(definition, bindingOutput);
        assertFalse(bindingOutput.getExtensibilityElements().isEmpty());
        SOAPBody soapBody =
                (SOAPBody) bindingOutput.getExtensibilityElements().get(0);
        assertNotNull(soapBody);
        assertEquals(new QName(SOAPBinding.WSDL_SOAP_NAMESPACE, "body"), soapBody.getElementType());
    }

    public void testPopulateWSDLBindingFault()
            throws Exception
    {
        WSDLFactory factory = WSDLFactory.newInstance();
        Definition definition = factory.newDefinition();
        BindingFault bindingFault = definition.createBindingFault();

        soapBinding.populateWSDLBindingFault(definition, bindingFault);
        assertFalse(bindingFault.getExtensibilityElements().isEmpty());
        SOAPFault soapFault =
                (SOAPFault) bindingFault.getExtensibilityElements().get(0);
        assertNotNull(soapFault);
        assertEquals(new QName(SOAPBinding.WSDL_SOAP_NAMESPACE, "fault"), soapFault.getElementType());
    }

    public void testSetStyle()
            throws Exception
    {
        try
        {
            soapBinding.setStyle("something");
            fail("No IllegalArgumentException thrown");
        }
        catch (IllegalArgumentException e)
        {
            // Expected behavior
        }
    }

    public void testSetUse()
            throws Exception
    {
        try
        {
            soapBinding.setUse("something");
            fail("No IllegalArgumentException thrown");
        }
        catch (IllegalArgumentException e)
        {
            // Expected behavior
        }
    }
}