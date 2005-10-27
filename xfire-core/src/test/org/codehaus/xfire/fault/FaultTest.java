package org.codehaus.xfire.fault;

import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;

/**
 * XFireTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class FaultTest
        extends AbstractXFireTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();

       
    }

    public void testSoap11()
            throws Exception
    {
        createService(Soap11.getInstance());
        
        Document response = invokeService("Echo", "/org/codehaus/xfire/echo11.xml");

        addNamespace("s", Soap11.getInstance().getNamespace());
        assertValid("//s:Fault/faultcode[text()='MustUnderstand']", response);
        assertValid("//s:Fault/faultstring[text()='CustomFault']", response);
        assertValid("//s:Fault/detail/test", response);
    }

    public void testSoap12()
        throws Exception
    {
        createService(Soap12.getInstance());
        
        Document response = invokeService("Echo", "/org/codehaus/xfire/echo12.xml");

        addNamespace("s", Soap12.getInstance().getNamespace());
        assertValid("//s:Fault/s:Code/s:Value[text()='soap:MustUnderstand']", response);
        assertValid("//s:Fault/s:Reason/s:Text[text()='CustomFault']", response);
        assertValid("//s:Fault/s:Detail/test", response);
    }
        
    private void createService(SoapVersion soapVersion)
    {
        ObjectServiceFactory osf = (ObjectServiceFactory) getServiceFactory();
        osf.setSoapVersion(soapVersion);
        
        Service service = osf.create(Echo.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, CustomFaultEcho.class);

        getServiceRegistry().register(service);
    }
}
