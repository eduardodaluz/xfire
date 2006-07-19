package org.codehaus.xfire.wsdl11;

import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.xml.namespace.QName;

import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilderExtension;

public class CustomExtension implements WSDLBuilderExtension
{

    public void extend(Definition definition, WSDLBuilder builder)
    {
        Message message = definition.createMessage();
        message.setQName(new QName("urn:foo", "Test"));
        message.setUndefined(false);
        
        definition.addMessage(message);
    }
    
}
