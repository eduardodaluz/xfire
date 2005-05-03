package org.codehaus.xfire.service;

import java.util.Collection;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.codehaus.xfire.fault.FaultHandler;
import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.Invoker;
import org.codehaus.xfire.service.binding.SOAPBinding;
import org.codehaus.xfire.service.binding.SOAPBindingFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.wsdl.WSDLWriter;

/**
 * Temporary adapter that wraps a <code>ServiceEndpoint</code> so that it follows the contract of the
 * <code>Service</code> interface.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class ServiceEndpointAdapter
        implements Service
{
    private ServiceEndpoint endpoint;

    public ServiceEndpointAdapter(ServiceEndpoint endpoint)
    {
        this.endpoint = endpoint;
    }

    public void addOperation(OperationInfo info)
    {
        getService().addOperation(info);
    }

    public OperationInfo getOperation(String name)
    {
        return getService().getOperation(name);
    }

    public Object getProperty(String name)
    {
        return endpoint.getProperty(name);
    }

    public void removeOperation(String string)
    {
        getService().removeOperation(string);
    }

    public void setProperty(String name, Object value)
    {
        endpoint.setProperty(name, value);
    }

    public BindingProvider getBindingProvider()
    {
        return endpoint.getBindingProvider();
    }

    public String getDefaultNamespace()
    {
        return getService().getName().getNamespaceURI();
    }

    public FaultHandler getFaultHandler()
    {
        return endpoint.getFaultHandler();
    }

    public FaultHandlerPipeline getFaultPipeline()
    {
        return endpoint.getFaultPipeline();
    }

    public Invoker getInvoker()
    {
        return endpoint.getInvoker();
    }

    public String getName()
    {
        return getService().getName().getLocalPart();
    }

    public Collection getOperations()
    {
        return getService().getOperations();
    }

    public HandlerPipeline getRequestPipeline()
    {
        return endpoint.getRequestPipeline();
    }

    public HandlerPipeline getResponsePipeline()
    {
        return endpoint.getResponsePipeline();
    }

    public int getScope()
    {
        return endpoint.getScope();
    }

    public Class getServiceClass()
    {
        return getService().getServiceClass();
    }

    private ServiceInfo getService()
    {
        return endpoint.getService();
    }

    public Handler getServiceHandler()
    {
        return endpoint.getServiceHandler();
    }

    public SoapVersion getSoapVersion()
    {
        return getSOAPBinding().getSoapVersion();
    }

    private SOAPBinding getSOAPBinding()
    {
        return ((SOAPBinding) endpoint.getBinding());
    }

    public String getStyle()
    {
        return getSOAPBinding().getStyle();
    }

    public String getUse()
    {
        return getSOAPBinding().getUse();
    }


    public WSDLWriter getWSDLWriter()
            throws WSDLException
    {
        return endpoint.getWSDLWriter();
    }

    public void setBindingProvider(BindingProvider bindingProvider)
    {
        endpoint.setBindingProvider(bindingProvider);
    }

    public void setDefaultNamespace(String defaultNamespace)
    {
        getService().setName(new QName(defaultNamespace, getService().getName().getLocalPart()));
    }

    public void setFaultHandler(FaultHandler faultHandler)
    {
        endpoint.setFaultHandler(faultHandler);
    }

    public void setFaultPipeline(FaultHandlerPipeline faultPipeline)
    {
        endpoint.setFaultPipeline(faultPipeline);
    }

    public void setInvoker(Invoker invoker)
    {
        endpoint.setInvoker(invoker);
    }

    public void setName(String name)
    {
        getService().setName(new QName(getService().getName().getNamespaceURI(), name));
    }

    public void setRequestPipeline(HandlerPipeline requestPipeline)
    {
        endpoint.setRequestPipeline(requestPipeline);
    }

    public void setResponsePipeline(HandlerPipeline responsePipeline)
    {
        endpoint.setResponsePipeline(responsePipeline);
    }

    public void setScope(int scope)
    {
        endpoint.setScope(scope);
    }

    public void setServiceHandler(Handler serviceHandler)
    {
        endpoint.setServiceHandler(serviceHandler);
    }

    public void setSoapVersion(SoapVersion soapVersion)
    {
        getSOAPBinding().setSoapVersion(soapVersion);
    }

    public void setStyle(String style)
    {
        SoapVersion oldVersion = getSOAPBinding().getSoapVersion();
        if (SoapConstants.STYLE_DOCUMENT.equals(style))
        {
            endpoint.setBinding(SOAPBindingFactory.createDocumentBinding(
                    new QName(getService().getName().getLocalPart() + "Binding"), oldVersion));
        }
        else if (SoapConstants.STYLE_RPC.equals(style))
        {
            endpoint.setBinding(SOAPBindingFactory.createRPCEncodedBinding(
                    new QName(getService().getName().getLocalPart() + "Binding"), oldVersion));
        }
        else if (SoapConstants.STYLE_WRAPPED.equals(style))
        {
            endpoint.setBinding(SOAPBindingFactory.createWrappedBinding(
                    new QName(getService().getName().getLocalPart() + "Binding"), oldVersion));
        }
        else
        {
            throw new IllegalArgumentException("Invalid style [" + style + "]");
        }
    }

    public void setUse(String use)
    {
        String oldStyle = getSOAPBinding().getStyle();
        SoapVersion oldVersion = getSOAPBinding().getSoapVersion();
        if (SoapConstants.USE_LITERAL.equals(use))
        {
            if (SoapConstants.STYLE_DOCUMENT.equals(oldStyle))
            {
                endpoint.setBinding(SOAPBindingFactory.createDocumentBinding(
                        new QName(getService().getName().getLocalPart() + "Binding"), oldVersion));
            }
            else if (SoapConstants.STYLE_WRAPPED.equals(oldStyle))
            {
                endpoint.setBinding(SOAPBindingFactory.createWrappedBinding(
                        new QName(getService().getName().getLocalPart() + "Binding"), oldVersion));
            }
        }
        else if (SoapConstants.USE_ENCODED.equals(use))
        {
            endpoint.setBinding(SOAPBindingFactory.createRPCEncodedBinding(
                    new QName(getService().getName().getLocalPart() + "Binding"), oldVersion));
        }
        else
        {
            throw new IllegalArgumentException("Invalid use [" + use + "]");
        }
    }


}

