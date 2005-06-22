package org.codehaus.xfire.xmlbeans;

import java.util.Collection;
import java.util.Iterator;

import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl11.WSDL11ParameterBinding;
import org.codehaus.xfire.wsdl11.builder.AbstractWSDL;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;

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

    public void createInputParts(WSDLBuilder builder, Message req, OperationInfo op)
    {
        writeParameters(builder, req, op.getInputMessage().getMessageParts());
    }

    public void createOutputParts(WSDLBuilder builder, Message req, OperationInfo op)
    {
        writeParameters(builder, req, op.getOutputMessage().getMessageParts());
    }
    
    private void writeParameters(WSDLBuilder builder,
                                 Message message, 
                                 Collection params)
    {
        for (Iterator itr = params.iterator(); itr.hasNext();)
        {
            MessagePartInfo param = (MessagePartInfo) itr.next();
            Class clazz = param.getTypeClass();
            QName pName = param.getName();

            Part part = builder.getDefinition().createPart();
            part.setName(pName.getLocalPart());
            part.setElementName(pName);

            String prefix = builder.getNamespacePrefix(pName.getNamespaceURI());
            builder.addNamespace(prefix, pName.getNamespaceURI());

            message.addPart(part);
        }
    }
}
