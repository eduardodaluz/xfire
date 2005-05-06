package org.codehaus.xfire.service.binding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.util.DepthXMLStreamReader;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.wsdl.SchemaType;
import org.codehaus.xfire.wsdl11.WSDL11ParameterBinding;
import org.codehaus.xfire.wsdl11.builder.AbstractWSDL;
import org.codehaus.yom.Attribute;
import org.codehaus.yom.Element;

public class DocumentBinding
    extends AbstractBinding
    implements WSDL11ParameterBinding
{
    public DocumentBinding()
    {
        setStyle(SoapConstants.STYLE_DOCUMENT);
        setUse(SoapConstants.USE_LITERAL);
    }

    public Object[] read(MessageContext context) throws XFireFault
    {
        ServiceEndpoint endpoint = context.getService();
        
        List parameters = new ArrayList();
        DepthXMLStreamReader dr = new DepthXMLStreamReader(context.getXMLStreamReader());

        while (STAXUtils.toNextElement(dr))
        {
            MessagePartInfo p = findMessagePart(endpoint, dr.getName());
            
            if (p == null)
            {
                throw new XFireFault("Parameter " + dr.getName() + " does not exist!", 
                                     XFireFault.SENDER);
            }

            parameters.add( getBindingProvider().readParameter(p, context) );
        }

        context.setProperty(OPERATION_KEY, 
                            findOperation(endpoint, parameters.size()) );
        
        return parameters.toArray();
    }

    public void write(Object[] values, MessageContext context) throws XFireFault
    {
        int i = 0;
        for(Iterator itr = getOperation(context).getOutputMessage().getMessageParts().iterator(); itr.hasNext();)
        {
            MessagePartInfo outParam = (MessagePartInfo) itr.next();
            
            getBindingProvider().writeParameter(outParam, context, values[i]);
            i++;
        }
    }

    protected OperationInfo findOperation(ServiceEndpoint endpoint, int i)
    {
        for ( Iterator itr = endpoint.getService().getOperations().iterator(); itr.hasNext(); )
        {
            OperationInfo o = (OperationInfo) itr.next();
            if ( o.getInputMessage().getMessageParts().size() == i )
                return o;
        }
        
        return null;
    }
    
    protected MessagePartInfo findMessagePart(ServiceEndpoint endpoint, QName name)
    {
        for ( Iterator itr = endpoint.getService().getOperations().iterator(); itr.hasNext(); )
        {
            OperationInfo op = (OperationInfo) itr.next();
            MessagePartInfo p = op.getInputMessage().getMessagePart(name);
            
            if ( p != null )
                return p;
        }
        return null;
    }
    
    
    public void createInputParts(ServiceEndpoint endpoint, 
                                 AbstractWSDL wsdl,
                                 Message req, 
                                 OperationInfo op)
    {
        writeParameters(endpoint, wsdl, req, op.getInputMessage().getMessageParts());
    }

    public void createOutputParts(ServiceEndpoint endpoint, 
                                  AbstractWSDL wsdl,
                                  Message req, 
                                  OperationInfo op)
    {
        writeParameters(endpoint, wsdl, req, op.getOutputMessage().getMessageParts());
    }

    private void writeParameters(ServiceEndpoint service, 
                                 AbstractWSDL wsdl,
                                 Message message, 
                                 Collection params)
    {
        for (Iterator itr = params.iterator(); itr.hasNext();)
        {
            MessagePartInfo param = (MessagePartInfo) itr.next();
            Class clazz = param.getTypeClass();
            QName pName = param.getName();
            SchemaType type = getBindingProvider().getSchemaType(service, param);

            wsdl.addDependency(type);
            
            QName schemaType = type.getSchemaType();

            Part part = wsdl.getDefinition().createPart();
            part.setName(pName.getLocalPart());

            if (type.isComplex())
            {
                part.setElementName(pName);

                Element schemaEl = wsdl.createSchemaType(wsdl.getInfo().getTargetNamespace());
                Element element = new Element(AbstractWSDL.elementQ, SoapConstants.XSD);
                schemaEl.appendChild(element);

                element.addAttribute(new Attribute("name", pName.getLocalPart()));

                String prefix = wsdl.getNamespacePrefix(schemaType.getNamespaceURI());
                wsdl.addNamespace(prefix, schemaType.getNamespaceURI());

                element.addAttribute(new Attribute("type", 
                                                   prefix + ":" + schemaType.getLocalPart()));
            }
            else
            {
                part.setElementName(type.getSchemaType());
            }

            message.addPart(part);
        }
    }
}
