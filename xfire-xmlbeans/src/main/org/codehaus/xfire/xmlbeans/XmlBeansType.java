package org.codehaus.xfire.xmlbeans;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.soap.handler.ReadHeadersHandler;
import org.codehaus.xfire.util.STAXUtils;
import org.jdom.Element;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 13, 2004
 */
public class XmlBeansType 
    extends Type
{
    private SchemaType schemaType;

    public XmlBeansType()
    {
    }
    
    public XmlBeansType(SchemaType schemaType)
    {
        this.schemaType = schemaType;
        setTypeClass(schemaType.getJavaClass());
    }

    public XmlBeansType(Class clazz)
    {
        this.schemaType = XmlBeans.typeForClass(clazz);
        setTypeClass(clazz);
    }

    public void writeSchema(Element root)
    {
    }
    
    public boolean isComplex()
    {
        return !schemaType.isPrimitiveType();
    }

    public boolean isAbstract()
    {
        return schemaType.isAbstract();
    }

    public boolean isWriteOuter()
    {
        return false;
    }

    public Set getDependencies()
    {
        SchemaProperty[] properties = schemaType.getProperties();
        HashSet deps = new HashSet();
        for (int i = 0; i < properties.length; i++)
        {
            SchemaType etype = properties[i].getType();
            SchemaProperty[] iprops = etype.getElementProperties();
            for (int j = 0; j < iprops.length; j++)
            {
                SchemaType itype = iprops[j].getType();
                
                testAndAddType(deps, itype);
            }
            
            testAndAddType(deps, etype.getBaseType());
            testAndAddType(deps, etype.getBaseEnumType());
        }
        return deps;
    }

    private void testAndAddType(HashSet deps, SchemaType itype)
    {
        if (itype != null && !itype.isPrimitiveType() && itype.getSourceName() != null)
        {
            deps.add(new XmlBeansType(itype));
        }
    }

    public QName getSchemaType()
    {
        if (schemaType.isDocumentType())
            return schemaType.getDocumentElementName();
        else if (schemaType.getName() != null)
            return schemaType.getName();
        else
        {
            // No name for this type, use outer type (and recur up if same)
            SchemaType outer = schemaType.getOuterType();
            while (outer != null)
            {
                if (outer.isDocumentType())
                    return outer.getDocumentElementName();
                else if (outer.getName() != null)
                    return outer.getName();
                else
                    outer = outer.getOuterType();
            }
            
            // No outer type, no type on this, should not be possible, so explode
            throw new XFireRuntimeException("No type name is defined for <" + schemaType + "> " +
                                            "and no outer type containing the inline type -- this " +
                                            "should not be possible to be a legally defined schema");
        }
    }

    public Object readObject(MessageReader mreader, MessageContext context)
        throws XFireFault
    {
        try
        {
            XMLStreamReader reader = ((ElementReader) mreader).getXMLStreamReader();
            XmlObject parsed = XmlObject.Factory.parse(reader);
            
            /* Add namespace declarations from the XMLStreamReader NamespaceContext.
             * This is important when values reference QNames. For instance, 
             * xsi:type="xsd:string". If the xsd namespace is declared on the SOAP
             * envelope then XMLBeans won't pick up. 
             */
            XmlCursor cursor = parsed.newCursor();
            try
            {
                cursor.toFirstContentToken();
                Map namespaces = (Map) context.getProperty(ReadHeadersHandler.DECLARED_NAMESPACES);
                for (Iterator itr = namespaces.entrySet().iterator(); itr.hasNext();)
                {
                    Map.Entry entry = (Map.Entry) itr.next();
                    cursor.insertNamespace((String) entry.getKey(), (String) entry.getValue());
                }
            }
            finally
            {
                cursor.dispose();
            }

            return parsed;
        }
        catch( XmlException e )
        {
            throw new XFireFault("Could not read request.", e, XFireFault.SENDER);
        }
    }

    public void writeObject(Object value, MessageWriter writer, MessageContext context)
        throws XFireFault
    {
        try
        {
            XmlObject obj = (XmlObject) value; 
       
            XmlCursor cursor = obj.newCursor();
            
            STAXUtils.copy(cursor.newXMLStreamReader(), 
                            ((ElementWriter) writer).getXMLStreamWriter());
        } 
        catch (XMLStreamException e)
        {
            throw new XFireFault("Could not write response.", e, XFireFault.SENDER);
        }
    }
}