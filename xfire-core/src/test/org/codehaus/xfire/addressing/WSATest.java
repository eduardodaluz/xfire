package org.codehaus.xfire.addressing;

import javax.xml.namespace.QName;

import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;
import org.codehaus.yom.stax.StaxBuilder;

public class WSATest
    extends AbstractXFireTest
{
    public void testEPRs() throws Exception
    {
        StaxBuilder builder = new StaxBuilder();
        Document doc =builder.build(getResourceAsStream("/org/codehaus/xfire/addressing/EPR1.xml"));
        
        AddressingHeadersFactory200502 factory = new AddressingHeadersFactory200502();
        EndpointReference epr = factory.createEPR(doc.getRootElement());
        assertEquals("http://example.com/fabrikam/acct", epr.getAddress());
        assertEquals(new QName("http://example.com/fabrikam", "Inventory"), epr.getInterfaceName());
        assertNull(epr.getEndpointName());
        assertNull(epr.getServiceName());
    }
    
    public void test2005Headers() throws Exception
    {
        StaxBuilder builder = new StaxBuilder();
        Document doc =builder.build(getResourceAsStream("/org/codehaus/xfire/addressing/200502Headers1.xml"));
        
        AddressingHeadersFactory200502 factory = new AddressingHeadersFactory200502();
        
        assertTrue(factory.hasHeaders(doc.getRootElement()));
        
        AddressingHeaders headers = factory.createHeaders(doc.getRootElement());
        assertEquals("http://example.com/6B29FC40-CA47-1067-B31D-00DD010662DA", 
                     headers.getMessageID());
        
        assertNotNull(headers.getReplyTo());
        assertEquals("http://example.com/business/client1",
                     headers.getReplyTo().getAddress());
        assertEquals("http://example.com/fabrikam/Purchasing",
                     headers.getTo());
        assertEquals("http://example.com/fabrikam/SubmitPO", headers.getAction());
    }
    

    public void test200408Headers() throws Exception
    {
        StaxBuilder builder = new StaxBuilder();
        Document doc =builder.build(getResourceAsStream("/org/codehaus/xfire/addressing/200408Headers1.xml"));
        
        AddressingHeadersFactory200408 factory = new AddressingHeadersFactory200408();
        
        assertTrue(factory.hasHeaders(doc.getRootElement()));
        
        AddressingHeaders headers = factory.createHeaders(doc.getRootElement());
        assertEquals("http://fabrikam123.example/mail/DeleteAck", headers.getAction());
        assertEquals("http://business456.example/client1", headers.getTo());
        assertEquals("uuid:aaaabbbb-cccc-dddd-eeee-wwwwwwwwwww", headers.getMessageID());
        assertEquals("uuid:aaaabbbb-cccc-dddd-eeee-ffffffffffff", headers.getRelatesTo());
        
        assertNotNull(headers.getReplyTo());
        EndpointReference ref = headers.getReplyTo();
        assertEquals("http://business456.example/client1", ref.getAddress());

        assertNotNull(ref.getReferenceParameters());
        assertEquals(1, ref.getReferenceParameters().size());
        
        assertNotNull(ref.getReferenceProperties());
        assertEquals(1, ref.getReferenceProperties().size());
        
        Element header = new Element("s:Header", Soap11.getInstance().getNamespace());
        doc = new Document(header);
        
        factory.writeHeaders(header, headers);
        System.out.println(header.toXML());
        addNamespace("wsa", WSAConstants.WSA_NAMESPACE_200408);
        assertValid("//wsa:Action[text()='" + headers.getAction() + "']", header);
        assertValid("//wsa:MessageID[text()='" + headers.getMessageID() + "']", header);
        assertValid("//wsa:ReplyTo/wsa:Address[text()='" + headers.getReplyTo().getAddress() + "']", header);
    }
}
