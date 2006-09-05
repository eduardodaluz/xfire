package org.codehaus.xfire.jaxb2;

import javax.xml.bind.annotation.XmlType;

import org.codehaus.xfire.client.XFireProxyFactory;
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
        endpoint = builder.create(AccountServiceImpl.class);
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
