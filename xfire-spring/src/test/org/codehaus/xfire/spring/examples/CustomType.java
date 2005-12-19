package org.codehaus.xfire.spring.examples;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.soap.SoapConstants;

public class CustomType extends Type
{
    public CustomType()
    {
        setTypeClass(String.class);
        setSchemaType(new QName(SoapConstants.XSD, "string"));
    }
    
    public Object readObject(MessageReader reader, MessageContext context)
        throws XFireFault
    {
        System.out.println(reader.getValue());
        
        return null;
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
        throws XFireFault
    {
        // TODO Auto-generated method stub
        
    }

}
