package org.codehaus.xfire.wsdl11.builder;

import java.io.IOException;
import java.io.OutputStream;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.xfire.wsdl11.WSDL11ParameterBinding;

/**
 * An adapter for a <code>WSDLBuilder</code> so that it conforms to the <code>WSDLWriter</code> interface.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class WSDLBuilderAdapter
        implements WSDLWriter
{
    private WSDLBuilderFactory wsdlBuilderFactory;
    private Service service;
    private TransportManager transportManager;
    private WSDL11ParameterBinding paramBinding;
    
    public WSDLBuilderAdapter(WSDLBuilderFactory wsdlBuilderFactory,
                              Service service, 
                              TransportManager transports,
                              WSDL11ParameterBinding paramBinding)
    {
        this.wsdlBuilderFactory = wsdlBuilderFactory;
        this.service = service;
        this.transportManager = transports;
        this.paramBinding = paramBinding;
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
        wsdlBuilderFactory.createWSDLBuilder(service, paramBinding, transportManager).write(out);
    }

}
