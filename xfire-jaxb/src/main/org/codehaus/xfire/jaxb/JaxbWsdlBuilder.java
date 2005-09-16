package org.codehaus.xfire.jaxb;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.wsdl11.WSDL11ParameterBinding;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;

import javax.wsdl.WSDLException;

/**
 * User: chris
 * Date: Aug 12, 2005
 * Time: 4:04:24 PM
 */
public class JaxbWsdlBuilder extends WSDLBuilder
{
    JaxbSchemaProvider jaxbSchemaProvider;

    public JaxbWsdlBuilder(Service service, TransportManager transportManager, WSDL11ParameterBinding paramBinding, JaxbSchemaProvider jaxbSchemaProvider) throws WSDLException
    {
        super(service, transportManager, paramBinding);

        this.jaxbSchemaProvider = jaxbSchemaProvider;
    }

    public void addDependency(org.codehaus.xfire.wsdl.SchemaType type)
    {
        if (!hasDependency(type))
        {
            if (type instanceof JaxbType)
            {
                JaxbType jaxbType = (JaxbType) type;

                Element schema = jaxbSchemaProvider.getSchema(jaxbType, this);
                schema.detach();
                setSchema(jaxbType.getSchemaType().getNamespaceURI(), schema);
            }

        }
        super.addDependency(type);
    }


}
