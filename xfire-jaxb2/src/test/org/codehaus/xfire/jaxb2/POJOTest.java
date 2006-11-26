package org.codehaus.xfire.jaxb2;

import javax.xml.bind.annotation.XmlType;

import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;
import org.jdom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class POJOTest
        extends AbstractXFireTest
{
    private Service endpoint;
    private ObjectServiceFactory builder;

    public void setUp()
            throws Exception
    {
        super.setUp();

        builder = new JaxbServiceFactory();
        endpoint = builder.create(AccountServiceImpl.class);
        endpoint.setProperty(WSDLBuilder.REMOVE_ALL_IMPORTS, "true");
        getServiceRegistry().register(endpoint);
    }

    public void testClientAndHeaders() throws Exception
    {
        AccountService client = (AccountService) 
            new XFireProxyFactory(getXFire()).create(endpoint, "xfire.local://AccountService");
        
        client.auth("123", "text");
    }
    
    
    public void testWsdl() throws Exception
    {
        Document doc = getWSDLDocument("AccountService");

        addNamespace("xsd", SoapConstants.XSD);
        
        assertValid("//xsd:schema[@targetNamespace='urn:account']/xsd:complexType[@name='Acct']", doc);
        assertInvalid("//xsd:import", doc);
    }
    
    @XmlType(name="Acct", namespace="urn:account")
    public static class Account
    {
        private String id;
        private Customer customer;
        
        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public Customer getCustomer()
        {
            return customer;
        }

        public void setCustomer(Customer customer)
        {
            this.customer = customer;
        }
        
        
    }
    

    @XmlType(name="Customer", namespace="urn:customer")
    public static class Customer
    {
        private String id;

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }
        
    }
}
