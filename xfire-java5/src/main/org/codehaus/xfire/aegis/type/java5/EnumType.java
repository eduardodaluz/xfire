package org.codehaus.xfire.aegis.type.java5;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

public class EnumType
    extends Type
{
    @Override
    public Object readObject(MessageReader reader, MessageContext context)
        throws XFireFault
    {
        String value = reader.getValue();

        return Enum.valueOf(getTypeClass(), value);
    }

    @Override
    public void writeObject(Object object, MessageWriter writer, MessageContext context)
        throws XFireFault
    {
        writer.writeValue(((Enum) object).toString());
    }

    @Override
    public void setTypeClass(Class typeClass)
    {
        if (!typeClass.isEnum())
        {
            throw new XFireRuntimeException("Type class must be an enum.");
        }
        
        super.setTypeClass(typeClass);
    }

    @Override
    public void writeSchema(Element root)
    {
        Namespace xsd = Namespace.getNamespace(SoapConstants.XSD_PREFIX, SoapConstants.XSD);
        
        Element simple = new Element("simpleType",xsd );
        simple.setAttribute(new Attribute("name", getSchemaType().getLocalPart()));
        root.addContent(simple);
        
        Element restriction = new Element("restriction", xsd);
        restriction.setAttribute(new Attribute("base", SoapConstants.XSD_PREFIX + ":string"));
        simple.addContent(restriction);
        
        Object[] constants = getTypeClass().getEnumConstants();

        for (Object constant : constants)
        {
            Element enumeration = new Element("enumeration", xsd);
            enumeration.setAttribute(new Attribute("value", ((Enum) constant).toString()));
            restriction.addContent(enumeration);
        }
    }

    @Override
    public boolean isComplex()
    {
        return true;
    }
}
