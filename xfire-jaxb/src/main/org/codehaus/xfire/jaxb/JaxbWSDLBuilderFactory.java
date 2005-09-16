package org.codehaus.xfire.jaxb;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.wsdl11.WSDL11ParameterBinding;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilderFactory;
import org.codehaus.yom.Document;

import javax.wsdl.WSDLException;
import javax.xml.stream.XMLStreamException;
import java.util.List;

/**
 * User: chris
 * Date: Aug 18, 2005
 * Time: 6:20:34 PM
 */
public class JaxbWSDLBuilderFactory implements WSDLBuilderFactory
{
    Document[] schemas;
    private JaxbSchemaProvider jaxbSchemaProvider;

    public JaxbWSDLBuilderFactory(List schemaLocations) throws XMLStreamException
    {
        jaxbSchemaProvider = new JaxbSchemaProvider(schemaLocations);
    }

    public WSDLBuilder createWSDLBuilder(Service service, WSDL11ParameterBinding paramBinding, TransportManager transportManager)
    {
        try
        {
            return new JaxbWsdlBuilder(service, transportManager, paramBinding, jaxbSchemaProvider);
        } catch (WSDLException e)
        {
            throw new XFireRuntimeException("error instantiating jaxbwsdlfactory", e);
        }
    }

    List schemaLocations;

}
