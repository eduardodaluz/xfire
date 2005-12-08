package org.codehaus.xfire.jaxws.dispatch;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import org.codehaus.xfire.jaxws.AbstractJAXWSHttpTest;
import org.codehaus.xfire.jaxws.services.EchoImpl;
import org.codehaus.xfire.util.DOMUtils;
import org.w3c.dom.Document;

import services.echo.EchoService;

public class SourceDispatchTest
    extends AbstractJAXWSHttpTest
{
    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        Endpoint.publish("http://localhost:8191/Echo", new EchoImpl());
    }

    public void testPayloadSourceDispatch()
        throws Exception
    {
        EchoService service = new EchoService();

        Dispatch<Source> d = service.createDispatch(new QName("urn:echo:wrapped", "EchoHttpPort"),
                                                    Source.class,
                                                    Service.Mode.PAYLOAD);
        assertNotNull(d);
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();

        Document document = DOMUtils.readXml(getResourceAsStream("echo-payload.xml"));

        DOMSource source = new DOMSource(document.getDocumentElement());
        
        Source object = d.invoke(source);
        assertNotNull(object);
    }
}
