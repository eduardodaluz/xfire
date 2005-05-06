package org.codehaus.xfire.wsdl11.builder;

import java.io.IOException;
import java.io.OutputStream;

import javax.wsdl.WSDLException;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.wsdl.WSDLWriter;

/**
 * An adapter for a <code>WSDLBuilder</code> so that it conforms to the <code>WSDLWriter</code> interface.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class WSDLBuilderAdapter
        implements WSDLWriter
{
    private ServiceEndpoint service;
    private TransportManager transportManager;
    
    public WSDLBuilderAdapter(ServiceEndpoint service, TransportManager transports)
    {
        this.service = service;
        this.transportManager = transports;
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
        try
        {
            new WSDLBuilder(service, transportManager.getTransports(service.getName())).write(out);
        }
        catch (WSDLException e)
        {
            throw new XFireRuntimeException("Couldn't build wsdl document.", e);
        }
    }
}
