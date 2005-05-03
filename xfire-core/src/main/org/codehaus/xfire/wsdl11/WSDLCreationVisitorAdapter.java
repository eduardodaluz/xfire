package org.codehaus.xfire.wsdl11;

import java.io.IOException;
import java.io.OutputStream;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;

import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.wsdl.WSDLCreationException;
import org.codehaus.xfire.wsdl.WSDLWriter;

/**
 * An adapter for a <code>WSDLCreationVisitor</code> so that it conforms to the <code>WSDLWriter</code> interface. The
 * WSDL definition created by the visitor is lazily created and cached.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class WSDLCreationVisitorAdapter
        implements WSDLWriter
{
    private ServiceEndpoint endpoint;
    private Definition definition;

    /**
     * Initializes a new instance of the <code>WSDLCreationVisitorAdapter</code> with the given endpoint.
     *
     * @param endpoint the endpoint to visit.
     */
    public WSDLCreationVisitorAdapter(ServiceEndpoint endpoint)
    {
        this.endpoint = endpoint;
    }

    /**
     * Write the WSDL to an OutputStream.
     *
     * @param out The OutputStream.
     * @throws java.io.IOException
     */
    public void write(OutputStream out)
            throws IOException
    {
        Definition definition = getDefinition();
        try
        {
            WSDLFactory factory = WSDLFactory.newInstance();
            javax.wsdl.xml.WSDLWriter wsdlWriter = factory.newWSDLWriter();
            wsdlWriter.writeWSDL(definition, out);
        }
        catch (WSDLException e)
        {
            throw new WSDLCreationException("Could not create WSDLFactory", e);
        }
    }

    /**
     * Lazily creates a defintion, if not present.
     *
     * @return the definition.
     */
    private Definition getDefinition()
    {
        if (definition == null)
        {
            WSDLCreationVisitor visitor = new WSDLCreationVisitor();
            endpoint.accept(visitor);
            definition = visitor.getDefinition();
        }
        return definition;
    }
}
