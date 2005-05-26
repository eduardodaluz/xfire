package org.codehaus.xfire.xmlbeans;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLBeansWSDLBuilder
    extends WSDLBuilder
{
    public XMLBeansWSDLBuilder(Service service, 
                               Collection transports,
                               Document schema) throws WSDLException
    {
        this(service, transports, new Document[] {schema});
    }

    public XMLBeansWSDLBuilder(Service service, 
                               Collection transports,
                               Document[] schemas) throws WSDLException
    {
        super(service, transports, new XMLBeansParameterBinding());
        
        Definition def = getDefinition();

        Types types = def.createTypes();
        def.setTypes(types);
        
        for ( int i = 0; i < schemas.length; i++ )
        {
            Element schemaEl = schemas[i].getDocumentElement();
            
            UnknownExtensibilityElement e = new UnknownExtensibilityElement();
            e.setElement(schemaEl);

            types.addExtensibilityElement(e);
        }
    }

    protected void writeDocument()
        throws WSDLException
    {
        // Do nothing.
    }

    public void write(OutputStream out)
        throws IOException
    {
        WSDLWriter writer;
        try
        {
            writer = WSDLFactory.newInstance().newWSDLWriter();
            
            writer.writeWSDL(getDefinition(), out);
        }
        catch (WSDLException e)
        {
            e.printStackTrace();
        }
    } 
}
