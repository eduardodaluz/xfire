package org.codehaus.xfire.service.binding;


import org.codehaus.xfire.service.Service;
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
        Service service = objectServiceFactory.create(Echo.class);
        assertNotNull(service);
        assertEquals("Echo", service.getName());
        assertEquals("http://binding.service.xfire.codehaus.org", service.getDefaultNamespace());
        assertEquals(SoapConstants.STYLE_WRAPPED, service.getStyle());
        assertEquals(SoapConstants.USE_LITERAL, service.getUse());
        assertEquals(Soap11.getInstance(), service.getSoapVersion());
    }

    public void testCreateVersionStyleUse()
            throws Exception
    {
        Service service = objectServiceFactory.create(Echo.class,
                                                      Soap12.getInstance(),
                                                      SoapConstants.STYLE_RPC,
                                                      SoapConstants.USE_ENCODED);
        assertNotNull(service);
        assertEquals("Echo", service.getName());
        assertEquals("http://binding.service.xfire.codehaus.org", service.getDefaultNamespace());
        assertEquals(SoapConstants.STYLE_RPC, service.getStyle());
        assertEquals(SoapConstants.USE_ENCODED, service.getUse());
        assertEquals(Soap12.getInstance(), service.getSoapVersion());
    }


    public void testCreateNameNamespaceVersionStyleUseEncodingStyle()
            throws Exception
    {
        Service service = objectServiceFactory.create(Echo.class,
                                                      "EchoService",
                                                      "http://xfire.codehaus.org",
                                                      Soap12.getInstance(),
                                                      SoapConstants.STYLE_RPC,
                                                      SoapConstants.USE_ENCODED,
                                                      null);
        assertNotNull(service);
        assertEquals("EchoService", service.getName());
        assertEquals("http://xfire.codehaus.org", service.getDefaultNamespace());
        assertEquals(SoapConstants.STYLE_RPC, service.getStyle());
        assertEquals(SoapConstants.USE_ENCODED, service.getUse());
        assertEquals(Soap12.getInstance(), service.getSoapVersion());
    }

    public void testCreateNameNamespaceNull()
            throws Exception
    {
        Service service = objectServiceFactory.create(Echo.class,
                                                      null,
                                                      null,
                                                      Soap12.getInstance(),
                                                      SoapConstants.STYLE_RPC,
                                                      SoapConstants.USE_ENCODED,
                                                      null);
        assertNotNull(service);
        assertEquals("Echo", service.getName());
        assertEquals("http://binding.service.xfire.codehaus.org", service.getDefaultNamespace());
        assertEquals(SoapConstants.STYLE_RPC, service.getStyle());
        assertEquals(SoapConstants.USE_ENCODED, service.getUse());
        assertEquals(Soap12.getInstance(), service.getSoapVersion());

    }

}