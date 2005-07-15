package org.codehaus.xfire.wsdl11.builder;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

import org.codehaus.xfire.XFireRuntimeException;
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
    private Service service;
    private TransportManager transportManager;
    private WSDL11ParameterBinding paramBinding;
    private Class wsdlBuilder;
    
    public WSDLBuilderAdapter(Class wsdlBuilder,
                              Service service, 
                              TransportManager transports,
                              WSDL11ParameterBinding paramBinding)
    {
        this.service = service;
        this.transportManager = transports;
        this.paramBinding = paramBinding;
        this.wsdlBuilder = wsdlBuilder;
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
        createWSDLBuilder().write(out);
    }
    
    public WSDLBuilder createWSDLBuilder()
    {
        try
        {
            Constructor c = wsdlBuilder.getConstructor(new Class[] { Service.class,
                    TransportManager.class, WSDL11ParameterBinding.class });

            return (WSDLBuilder) c.newInstance(new Object[] { service, transportManager,
                    paramBinding });
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException("Could not create wsdl builder", e);
        }
    }
}
