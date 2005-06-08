package org.codehaus.xfire.service.binding;


import javax.xml.namespace.QName;

import org.codehaus.xfire.service.Service;
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
        Service endpoint = objectServiceFactory.create(Echo.class);
        assertNotNull(endpoint);
        ServiceInfo service = endpoint.getServiceInfo();
        assertEquals(new QName("http://binding.service.xfire.codehaus.org", "Echo"), service.getName());
        assertEquals(Soap11.getInstance(), endpoint.getSoapVersion());
        
        AbstractBinding binding = (AbstractBinding) endpoint.getBinding();
        assertNotNull(binding);
        assertEquals(SoapConstants.STYLE_WRAPPED, binding.getStyle());
        assertEquals(SoapConstants.USE_LITERAL, binding.getUse());
    }

    public void testCreateVersionStyleUse()
            throws Exception
    {
        Service endpoint = objectServiceFactory.create(Echo.class,
                                                               Soap12.getInstance(),
                                                               SoapConstants.STYLE_RPC,
                                                               SoapConstants.USE_ENCODED);
        assertNotNull(endpoint);
        ServiceInfo service = endpoint.getServiceInfo();
        assertEquals(new QName("http://binding.service.xfire.codehaus.org", "Echo"), service.getName());
        assertEquals(Soap12.getInstance(), endpoint.getSoapVersion());
        
        AbstractBinding binding = (AbstractBinding) endpoint.getBinding();
        assertEquals(SoapConstants.STYLE_RPC, binding.getStyle());
        assertEquals(SoapConstants.USE_ENCODED, binding.getUse());
    }


    public void testCreateNameNamespaceVersionStyleUseEncodingStyle()
            throws Exception
    {
        Service endpoint = objectServiceFactory.create(Echo.class,
                                                               "EchoService",
                                                               "http://xfire.codehaus.org",
                                                               Soap12.getInstance(),
                                                               SoapConstants.STYLE_RPC,
                                                               SoapConstants.USE_ENCODED,
                                                               null);
        assertNotNull(endpoint);
        ServiceInfo service = endpoint.getServiceInfo();
        assertEquals(new QName("http://xfire.codehaus.org", "EchoService"), service.getName());
        assertEquals(Soap12.getInstance(), endpoint.getSoapVersion());
        
        AbstractBinding binding = (AbstractBinding) endpoint.getBinding();
        assertEquals(SoapConstants.STYLE_RPC, binding.getStyle());
        assertEquals(SoapConstants.USE_ENCODED, binding.getUse());
    }

    public void testCreateNameNamespaceNull()
            throws Exception
    {
        Service endpoint = objectServiceFactory.create(Echo.class,
                                                               null,
                                                               null,
                                                               Soap12.getInstance(),
                                                               SoapConstants.STYLE_RPC,
                                                               SoapConstants.USE_ENCODED,
                                                               null);
        assertNotNull(endpoint);
        ServiceInfo service = endpoint.getServiceInfo();
        assertEquals(new QName("http://binding.service.xfire.codehaus.org", "Echo"), service.getName());
        assertEquals(Soap12.getInstance(), endpoint.getSoapVersion());

        AbstractBinding binding = (AbstractBinding) endpoint.getBinding();
        assertEquals(SoapConstants.STYLE_RPC, binding.getStyle());
        assertEquals(SoapConstants.USE_ENCODED, binding.getUse());
    }
    
    public void testOverridenNames()
        throws Exception
    {
        Service service = getServiceFactory().create(OperationNameService.class);
        
        assertTrue( service.getServiceInfo().getOperation("doSomething") != null );
        assertTrue( service.getServiceInfo().getOperation("doSomething1") != null );
    }

    public class OperationNameService
    {
        public void doSomething() {}
        public void doSomething(String bleh) {}
    }

}