package org.codehaus.xfire.xmlbeans;

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.SchemaType;
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
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;
import org.codehaus.yom.Node;
import org.codehaus.yom.stax.StaxBuilder;
import org.codehaus.yom.xpath.YOMXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 13, 2004
 */
public class XmlBeansType 
    extends Type
{
    private SchemaType schemaType;

    private final static StaxBuilder builder = new StaxBuilder();
    private final static Log logger = LogFactory.getLog(XmlBeansType.class); 
    
    public XmlBeansType()
    {
    }
    
    public XmlBeansType(SchemaType schemaType)
    {
        this.schemaType = schemaType;
    }

    public XmlBeansType(Class clazz)
    {
        this.schemaType = XmlBeansServiceFactory.getSchemaType(clazz);
        setTypeClass(clazz);
    }

    public void writeSchema(Element root)
    {
        try
        {
            Element schema = builder.buildElement(null, getSchema().newXMLStreamReader());
            Document schemaDoc = new Document(schema);
            
            String ns = getSchemaType().getNamespaceURI();
            String expr = "//xsd:schema[@targetNamespace='" + ns + "']";
            
            if (schemaType.isAbstract())
                expr += "/xsd:complexType[@name=" + getSchemaType().getLocalPart() + "]";
            else
                expr += "/xsd:element[@name='" + getSchemaType().getLocalPart() + "']";
            
            List nodes = getMatches(schema, expr);
            if (nodes.size() == 0)
            {
                logger.warn("No schema found for " + expr);
                return;
            }
            
            Node node = (Node) nodes.get(0);
            node.detach();
            root.appendChild(node);
        }
        catch (XMLStreamException e)
        {
            throw new XFireRuntimeException("Couldn't parse schema.", e);
        }
    }

    private List getMatches(Object doc, String xpath)
    {
        try
        {
            XPath path = new YOMXPath(xpath);
            path.addNamespace("xsd", SoapConstants.XSD);
            List result = path.selectNodes(doc);
            return result;
        }
        catch(JaxenException e)
        {
            throw new XFireRuntimeException("Error evaluating xpath " + xpath, e);
        }
    }

    public XmlObject getSchema()
    {
        String name = schemaType.getSourceName();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            return XmlObject.Factory.parse(classLoader.getResourceAsStream("schema/src/" + name));
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException("Couldn't load schema.", e);
        }
    }
    
    public boolean isComplex()
    {
        return !schemaType.isPrimitiveType();
    }

    public boolean isAbstract()
    {
        return schemaType.isAbstract();
    }

    public Set getDependencies()
    {
        return null;
    }

    public QName getSchemaType()
    {
        return schemaType.getDocumentElementName();
    }

    public Object readObject(MessageReader reader, MessageContext context)
        throws XFireFault
    {
        try
        {
            return XmlObject.Factory.parse(((ElementReader)reader).getXMLStreamReader());
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
            if (cursor.toFirstChild() && cursor.toFirstChild())
            {
                do
                {
                    STAXUtils.copy(cursor.newXMLStreamReader(), 
                                   ((ElementWriter) writer).getXMLStreamWriter());
                }
                while(cursor.toNextSibling());
            }
        } 
        catch (XMLStreamException e)
        {
            throw new XFireFault("Could not write response.", e, XFireFault.SENDER);
        }
    }
}