package org.codehaus.xfire.exchange;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.yom.Element;
import org.codehaus.yom.Elements;

public abstract class AbstractMessageExchange
    implements MessageExchange
{

    private OperationInfo operation;

    /**
     * Validates that the mustUnderstand and role headers are processed correctly.
     *
     * @param context
     * @throws XFireFault
     */
    protected void validateHeaders(MessageContext context)
            throws XFireFault
    {
        if (context.getInMessage().getHeader() == null)
            return;

        SoapVersion version = context.getInMessage().getSoapVersion();
        Elements elements = context.getInMessage().getHeader().getChildElements();
        for (int i = 0; i < elements.size(); i++)
        {
            Element e = elements.get(i);
            String mustUnderstand = e.getAttributeValue("mustUnderstand",
                    version.getNamespace());

            if (mustUnderstand != null && mustUnderstand.equals("1"))
            {
                assertUnderstandsHeader(context, new QName(e.getNamespaceURI(), e.getLocalName()));
            }
        }
    }

    /**
     * Assert that a service understands a particular header.  If not, a fault is thrown.
     *
     * @param context
     * @param name
     * @throws XFireFault
     */
    protected void assertUnderstandsHeader(MessageContext context, QName name)
            throws XFireFault
    {
        Service endpoint = context.getService();
        if (endpoint.getInPipeline() != null &&
                understands(endpoint.getInPipeline(), name))
            return;

        if (endpoint.getOutPipeline() != null &&
                understands(endpoint.getOutPipeline(), name))
            return;

        throw new XFireFault("Header {" + name.getLocalPart() + "}" + name.getNamespaceURI()
                + " was not undertsood by the service.", XFireFault.MUST_UNDERSTAND);
    }

    /**
     * Determine if a particular pipeline undertands a header.
     *
     * @param pipeline
     * @param name
     * @return
     */
    private boolean understands(HandlerPipeline pipeline, QName name)
    {
        for (int i = 0; i < pipeline.size(); i++)
        {
            QName[] understoodQs = pipeline.getHandler(i).getUnderstoodHeaders();

            if (understoodQs != null)
            {
                for (int j = 0; j < understoodQs.length; j++)
                {
                    if (understoodQs[j].equals(name))
                        return true;
                }
            }
        }

        return false;
    }

    protected void invokeInPipeline(MessageContext context)
        throws Exception
    {
        Transport transport = context.getInMessage().getChannel().getTransport();
        if (transport != null)
            HandlerPipeline.invokePipeline(transport.getRequestPipeline(), context);
    
        if (context.getService() != null)
            HandlerPipeline.invokePipeline(context.getService().getInPipeline(), context);
    }

    protected void invokeOutPipeline(MessageContext context)
        throws Exception
    {
        Transport transport = context.getOutMessage().getChannel().getTransport();
        if (context.getService() != null)
            HandlerPipeline.invokePipeline(context.getService().getOutPipeline(), context);
    
        if (transport != null)
            HandlerPipeline.invokePipeline(transport.getResponsePipeline(), context);
    }

    protected void invokeFaultPipeline(XFireFault fault, MessageContext context)
    {
        Transport transport = context.getOutMessage().getChannel().getTransport();
        
        if (transport != null && transport.getFaultPipeline() != null)
        {
            transport.getFaultPipeline().handleFault(fault, context);
        }
    
        if (context.getService() != null && context.getService().getFaultPipeline() != null)
        {
            context.getService().getFaultPipeline().handleFault(fault, context);
        }
    }

    public OperationInfo getOperation()
    {
        return operation;
    }

    public void setOperation(OperationInfo operation)
    {
        this.operation = operation;
    }
}
