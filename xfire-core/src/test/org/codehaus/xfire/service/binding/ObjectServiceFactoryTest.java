package org.codehaus.xfire.service.binding;


import javax.xml.namespace.QName;

import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;

public class ObjectServiceFactoryTest
        extends AbstractXFireTest
{
    private ObjectServiceFactory objectServiceFactory;

    public void setUp()
            throws Exception
    {
        super.setUp();

        objectServiceFactory = new ObjectServiceFactory(getXFire().getTransportManager(),
                                                        new MessageBindingProvider());
    }

    public void testMakeServiceNameFromClassName()
            throws Exception
    {
        String serviceName = objectServiceFactory.makeServiceNameFromClassName(ObjectServiceFactoryTest.class);
        assertNotNull(serviceName);
        assertEquals("Invalid service name", "ObjectServiceFactoryTest", serviceName);
    }

    public void testCreateClass()
            throws Exception
    {
        ServiceEndpoint endpoint = objectServiceFactory.create(Echo.class);
        assertNotNull(endpoint);
        ServiceInfo service = endpoint.getService();
        assertEquals(new QName("http://binding.service.xfire.codehaus.org", "Echo"), service.getName());
        SOAPBinding binding = (SOAPBinding) endpoint.getBinding();
        assertNotNull(binding);
        assertEquals(SoapConstants.STYLE_WRAPPED, binding.getStyle());
        assertEquals(SoapConstants.USE_LITERAL, binding.getUse());
        assertEquals(Soap11.getInstance(), binding.getSoapVersion());
    }

    public void testCreateVersionStyleUse()
            throws Exception
    {
        ServiceEndpoint endpoint = objectServiceFactory.create(Echo.class,
                                                               Soap12.getInstance(),
                                                               SoapConstants.STYLE_RPC,
                                                               SoapConstants.USE_ENCODED);
        assertNotNull(endpoint);
        ServiceInfo service = endpoint.getService();
        assertEquals(new QName("http://binding.service.xfire.codehaus.org", "Echo"), service.getName());
        SOAPBinding binding = (SOAPBinding) endpoint.getBinding();
        assertEquals(SoapConstants.STYLE_RPC, binding.getStyle());
        assertEquals(SoapConstants.USE_ENCODED, binding.getUse());
        assertEquals(Soap12.getInstance(), binding.getSoapVersion());
    }


    public void testCreateNameNamespaceVersionStyleUseEncodingStyle()
            throws Exception
    {
        ServiceEndpoint endpoint = objectServiceFactory.create(Echo.class,
                                                               "EchoService",
                                                               "http://xfire.codehaus.org",
                                                               Soap12.getInstance(),
                                                               SoapConstants.STYLE_RPC,
                                                               SoapConstants.USE_ENCODED,
                                                               null);
        assertNotNull(endpoint);
        ServiceInfo service = endpoint.getService();
        assertEquals(new QName("http://xfire.codehaus.org", "EchoService"), service.getName());
        SOAPBinding binding = (SOAPBinding) endpoint.getBinding();
        assertEquals(SoapConstants.STYLE_RPC, binding.getStyle());
        assertEquals(SoapConstants.USE_ENCODED, binding.getUse());
        assertEquals(Soap12.getInstance(), binding.getSoapVersion());
    }

    public void testCreateNameNamespaceNull()
            throws Exception
    {
        ServiceEndpoint endpoint = objectServiceFactory.create(Echo.class,
                                                               null,
                                                               null,
                                                               Soap12.getInstance(),
                                                               SoapConstants.STYLE_RPC,
                                                               SoapConstants.USE_ENCODED,
                                                               null);
        assertNotNull(endpoint);
        ServiceInfo service = endpoint.getService();
        assertEquals(new QName("http://binding.service.xfire.codehaus.org", "Echo"), service.getName());
        SOAPBinding binding = (SOAPBinding) endpoint.getBinding();
        assertEquals(SoapConstants.STYLE_RPC, binding.getStyle());
        assertEquals(SoapConstants.USE_ENCODED, binding.getUse());
        assertEquals(Soap12.getInstance(), binding.getSoapVersion());

    }

}