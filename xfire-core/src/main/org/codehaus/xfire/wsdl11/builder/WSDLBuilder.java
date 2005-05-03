package org.codehaus.xfire.wsdl11.builder;

import java.util.Collection;

import javax.wsdl.WSDLException;

import org.codehaus.xfire.AbstractXFireComponent;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.binding.SOAPBinding;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.wsdl.WSDLCreationException;
import org.codehaus.xfire.wsdl.WSDLWriter;

/**
 * Create a WSDL document for a ObjectService.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Apr 7, 2004
 */
public class WSDLBuilder
        extends AbstractXFireComponent
{
    private TransportManager manager;

    public WSDLBuilder(TransportManager manager)
    {
        this.manager = manager;
    }

    /**
     * @param s The service WSDL is being generated for.
     * @return
     */
    public WSDLWriter createWSDLWriter(ServiceEndpoint endpoint)
    {
        Collection transports = manager.getTransports(endpoint.getName());
        SOAPBinding binding = (SOAPBinding) endpoint.getBinding();
        
        try
        {
            if (binding.getStyle().equals(SoapConstants.STYLE_WRAPPED)
                    && binding.getUse().equals(SoapConstants.USE_LITERAL))
            {
                return new WrappedWSDL(endpoint, transports);
            }
            else if (binding.getStyle().equals(SoapConstants.STYLE_DOCUMENT)
                    && binding.getUse().equals(SoapConstants.USE_LITERAL))
            {
                return new DocumentWSDL(endpoint, transports);
            }
            else if (binding.getStyle().equals(SoapConstants.STYLE_RPC)
                    && binding.getUse().equals(SoapConstants.USE_ENCODED))
            {
                return new RPCEncodedWSDL(endpoint, transports);
            }
            else
            {
                throw new UnsupportedOperationException("Service style/use combination is not supported: "
                                                        + binding.getStyle() + "/" + binding.getUse());
            }
        }
        catch (WSDLException e)
        {
            throw new WSDLCreationException("Could not create WSDLBuilder", e);
        }
    }
}
