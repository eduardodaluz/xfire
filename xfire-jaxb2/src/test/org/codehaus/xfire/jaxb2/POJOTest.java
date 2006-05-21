package org.codehaus.xfire.jaxb2;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
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
        endpoint = builder.create(AccountService.class);
        
        getServiceRegistry().register(endpoint);
    }

    public void testWsdl() throws Exception
    {
        Document doc = getWSDLDocument("AccountService");

        addNamespace("xsd", SoapConstants.XSD);
        
        assertValid("//xsd:schema[@targetNamespace='urn:account']/xsd:complexType[@name='Acct']", doc);
    }
    
    @WebService
    public static class AccountService
    {
        public Account getAccount()
        {
            return new Account();
        }
    }
    
    @XmlType(name="Acct", namespace="urn:account")
    public static class Account
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
