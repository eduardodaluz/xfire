package org.codehaus.xfire.xmlbeans;

import java.util.HashMap;
import java.util.Map;

import net.webservicex.WeatherData;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.type.DefaultTypeCreator;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.soap.handler.ReadHeadersHandler;
import org.codehaus.xfire.test.AbstractXFireTest;

public class XmlTypeTest
    extends AbstractXFireTest
{
    public void testNamespaces() throws Exception
    {
        XmlBeansType type = new XmlBeansType(WeatherData.class);
        
        Map nsmap = new HashMap();
        nsmap.put("xsd", SoapConstants.XSD);
        
        MessageContext context = new MessageContext();
        context.setProperty(ReadHeadersHandler.DECLARED_NAMESPACES, nsmap);
        
        type.readObject(new ElementReader(getResourceAsStream("/org/codehaus/xfire/xmlbeans/undeclaredns.xml")),
                        context);
    }

    public void testTypeCreator() throws Exception
    {
        XmlBeansTypeCreator typeCreator = new XmlBeansTypeCreator(new DefaultTypeCreator());
        
        XmlBeansType type = (XmlBeansType) typeCreator.createType(WeatherData.class);
     
        assertNotNull(type);
    }
}
