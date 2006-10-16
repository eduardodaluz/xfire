package org.codehaus.xfire.aegis.inheritance.intf;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.invoker.BeanInvoker;
import org.codehaus.xfire.util.LoggingHandler;
import org.codehaus.xfire.util.dom.DOMOutHandler;
import org.jdom.Document;

/**
 * This test ensures that we're handling inheritance of itnerfaces
 * correctly. Since we can't do multiple parent inheritance in XML
 * schema, which interfaces require, we just don't allow interface
 * inheritance period.
 * 
 * @author Dan Diephouse
 */
public class InterfaceInheritanceTest
    extends AbstractXFireAegisTest
{
    Service service;
    
    public void setUp()
        throws Exception
    {
        super.setUp();
        service = getServiceFactory().create(IInterfaceService.class);
        service.setInvoker(new BeanInvoker(new InterfaceService()));

        service.addOutHandler(new DOMOutHandler());
        service.addOutHandler(new LoggingHandler());
        getServiceRegistry().register(service);
    }

    public void testClient() throws Exception
    {
        IInterfaceService client = (IInterfaceService) new XFireProxyFactory(getXFire()).create(service, "xfire.local://IInterfaceService");
        
        IChild child = client.getChild();
        assertNotNull(child);
        assertEquals("child", child.getChildName());
        assertEquals("parent", child.getParentName());
        
        IParent parent = client.getChildViaParent();
        assertEquals("parent", parent.getParentName());
        assertFalse(parent instanceof IChild);

        IGrandChild grandChild = client.getGrandChild();
        assertEquals("parent", grandChild.getParentName());
        
        Document wsdl = getWSDLDocument("IInterfaceService");
        assertValid("//xsd:complexType[@name='IGrandChild']", wsdl);
        assertValid("//xsd:complexType[@name='IGrandChild']//xsd:element[@name='grandChildName']", wsdl);
        assertValid("//xsd:complexType[@name='IGrandChild']//xsd:element[@name='childName'][1]", wsdl);
        assertInvalid("//xsd:complexType[@name='IGrandChild']//xsd:element[@name='childName'][2]", wsdl);
        assertValid("//xsd:complexType[@name='IChild']", wsdl);
        assertValid("//xsd:complexType[@name='IParent']", wsdl);
        assertInvalid("//xsd:complexType[@name='IChild'][@abstract='true']", wsdl);
    }
}
