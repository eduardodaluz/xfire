package org.codehaus.xfire.xmlbeans;

import java.io.IOException;
import java.io.OutputStream;

import javax.wsdl.WSDLException;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.w3c.dom.Document;

/**
 * An adapter for a <code>WSDLBuilder</code> so that it conforms to the
 * <code>WSDLWriter</code> interface.
 * 
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma </a>
 */
public class XMLBeansWSDLBuilderAdapter
    implements WSDLWriter
{

    private Service service;

    private TransportManager transportManager;

    private Document[] schemas;

    public XMLBeansWSDLBuilderAdapter(Service service, TransportManager transports,
            Document[] schemas)
    {
        this.service = service;
        this.transportManager = transports;
        this.schemas = schemas;
    }

    public XMLBeansWSDLBuilderAdapter(Service service, TransportManager transports,
            Document schema)
    {
        this.service = service;
        this.transportManager = transports;
        this.schemas = new Document[] { schema };
    }

    /**
     * Write the WSDL to an OutputStream.
     * 
     * @param out
     *            The OutputStream.
     * @throws java.io.IOException
     */
    public void write(OutputStream out)
        throws IOException
    {
        try
        {
            new XMLBeansWSDLBuilder(service, 
                                    transportManager.getTransports(service.getName()),
                                    schemas).write(out);
        }
        catch (WSDLException e)
        {
            throw new XFireRuntimeException("Couldn't build wsdl document.", e);
        }
    }
}
