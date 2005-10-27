package org.codehaus.xfire.aegis.type.basic;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

public class TransformTest
    extends AbstractXFireAegisTest
{
    protected void setUp()
        throws Exception
    {
        super.setUp();

        Service service = getServiceFactory().create(TransformService.class);
        getServiceRegistry().register(service);
    }

    public void testTransform() throws Exception
    {
        Document response = invokeService("TransformService", "/org/codehaus/xfire/aegis/type/basic/Transform.xml");

        assertNotNull(response);
        assertNoFault(response);

        addNamespace("t", "http://basic.type.aegis.xfire.codehaus.org");
        assertValid("//t:transformResponse/t:out/t:in1/t:foo", response);
    }

    public void testTransformWSDL() throws Exception
    {
        Document doc = getWSDLDocument("TransformService");

        addNamespace("xsd", SoapConstants.XSD);

        assertValid("//xsd:element[@name='in0'][@type='xsd:anyType']", doc);
        assertValid("//xsd:element[@name='in1'][@type='xsd:anyType']", doc);
        assertValid("//xsd:element[@name='out'][@type='xsd:anyType']", doc);
    }
}
