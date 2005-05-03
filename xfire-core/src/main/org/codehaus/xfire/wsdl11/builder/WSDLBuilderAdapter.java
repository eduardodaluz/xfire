package org.codehaus.xfire.wsdl11.builder;

import java.io.IOException;
import java.io.OutputStream;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.wsdl.WSDLWriter;

/**
 * An adapter for a <code>WSDLBuilder</code> so that it conforms to the <code>WSDLWriter</code> interface.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class WSDLBuilderAdapter
        implements WSDLWriter
{
    private WSDLBuilder wsdlBuilder;
    private Service service;

    public WSDLBuilderAdapter(WSDLBuilder wsdlBuilder, Service service)
    {
        this.wsdlBuilder = wsdlBuilder;
        this.service = service;
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
        wsdlBuilder.createWSDLWriter(service).write(out);
    }
}
