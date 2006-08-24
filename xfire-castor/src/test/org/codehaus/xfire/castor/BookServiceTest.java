package org.codehaus.xfire.castor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;

public class BookServiceTest
    extends AbstractXFireTest
{
    private Service endpoint;

    private ObjectServiceFactory builder;

    public void setUp()
        throws Exception
    {
        super.setUp();
        // XXX set to stax-dev input factory as it was setting to woodstox input
        // factory that was throwing a ClassCastException
        System.setProperty("javax.xml.stream.XMLInputFactory",
                             "com.bea.xml.stream.MXParserFactory");

        CastorTypeMappingRegistry registry = new CastorTypeMappingRegistry();
        registry.setMappingFile("org/codehaus/xfire/castor/castor.xml");
        builder = new ObjectServiceFactory(getXFire().getTransportManager(),
                new AegisBindingProvider(registry));
        ArrayList schemas = new ArrayList();
        schemas.add(getTestFile("src/test-schemas/Book.xsd").getAbsolutePath());
        Map props = new HashMap();
        props.put(ObjectServiceFactory.SCHEMAS, schemas);

        endpoint = builder.create(BookService.class,
                                  "BookService",
                                  "http://xfire.codehaus.org/",
                                  props);

        getServiceRegistry().register(endpoint);
    }

    public void testWsdl()
        throws Exception
    {
        Document doc = getWSDLDocument("BookService");
        addNamespace("xsd", SoapConstants.XSD);
        assertValid("//xsd:schema[@targetNamespace='http://xfire.codehaus.org/']", doc);
    }

    public void testAddBookService()
        throws Exception
    {
        Document response = invokeService("BookService", "AddBook.xml");
        addNamespace("w", "http://xfire.codehaus.org/");
        assertValid("//s:Body/w:addBookResponse/w:out", response);
    }

    public void testFindBookService()
        throws Exception
    {

        Document response = invokeService("BookService", "FindBook.xml");
        addNamespace("w", "http://xfire.codehaus.org/");
        assertValid("//s:Body/w:findBookResponse/w:out", response);
    }

}