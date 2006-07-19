package org.codehaus.xfire.wsdl11;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.type.xml.JDOMDocumentTest.Echo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.BeanInvoker;
import org.codehaus.xfire.test.EchoImpl;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.xfire.wsdl11.builder.DefaultWSDLBuilderFactory;
import org.jdom.Document;

public class WSDLExtensionTest
    extends AbstractXFireAegisTest
{
    public void testExtensions()
        throws Exception
    {
        ObjectServiceFactory osf = (ObjectServiceFactory) getServiceFactory();
        DefaultWSDLBuilderFactory factory = (DefaultWSDLBuilderFactory) osf.getWsdlBuilderFactory();

        Service service = getServiceFactory().create(Echo.class);
        service.setInvoker(new BeanInvoker(new EchoImpl()));

        getServiceRegistry().register(service);
        
        List exts = new ArrayList();
        exts.add(new CustomExtension());
        factory.setWSDLBuilderExtensions(exts);

        Document wsdl = getWSDLDocument("Echo");
        addNamespace("w", WSDLWriter.WSDL11_NS);
        addNamespace("f", "urn:foo");
        assertValid("//w:message[@name='Test']", wsdl);
    }
}
