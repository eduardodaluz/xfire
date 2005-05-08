package org.codehaus.xfire.xmlbeans;

import java.util.Collection;
import java.util.Iterator;

import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl11.WSDL11ParameterBinding;
import org.codehaus.xfire.wsdl11.builder.AbstractWSDL;

public class XMLBeansParameterBinding
    implements WSDL11ParameterBinding
{
    public String getStyle()
    {
        return SoapConstants.STYLE_DOCUMENT;
    }

    public String getUse()
    {
        return SoapConstants.USE_LITERAL;
    }

    public void createInputParts(ServiceEndpoint service, AbstractWSDL wsdl, Message req, OperationInfo op)
    {
        writeParameters(service, wsdl, req, op.getInputMessage().getMessageParts());
    }

    public void createOutputParts(ServiceEndpoint service, AbstractWSDL wsdl, Message req, OperationInfo op)
    {
        writeParameters(service, wsdl, req, op.getOutputMessage().getMessageParts());
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

            Part part = wsdl.getDefinition().createPart();
            part.setName(pName.getLocalPart());
            part.setElementName(pName);

            String prefix = wsdl.getNamespacePrefix(pName.getNamespaceURI());
            wsdl.addNamespace(prefix, pName.getNamespaceURI());

            message.addPart(part);
        }
    }
}
