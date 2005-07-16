package org.codehaus.xfire.xmlbeans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.WSDLException;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.wsdl11.WSDL11ParameterBinding;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;
import org.codehaus.yom.stax.StaxBuilder;
import org.codehaus.yom.xpath.YOMXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

public class XmlBeansWSDLBuilder
    extends WSDLBuilder
{
    private final static StaxBuilder builder = new StaxBuilder();
    private static Map schemas = new HashMap();
    
    public XmlBeansWSDLBuilder(Service service, TransportManager tman, WSDL11ParameterBinding paramBinding) throws WSDLException
    {
        super(service, tman, paramBinding);
    }

    public void addDependency(org.codehaus.xfire.wsdl.SchemaType type)
    {
        if (!hasDependency(type))
        {
            if (type instanceof XmlBeansType)
            {
                XmlBeansType xbeanType = (XmlBeansType) type;

                Element schema = getSchema(xbeanType);
                schema.detach();
                setSchema(xbeanType.getSchemaType().getNamespaceURI(), schema);
            }
        }
        
        super.addDependency(type);
    }
    
    public Element getSchema(XmlBeansType xbeanType)
    {
        SchemaType type = XmlBeans.typeForClass(xbeanType.getTypeClass());
        String name = type.getSourceName();
        if (name == null) return null;

        Element schema = (Element) schemas.get(name); 
        if (schema != null) return schema;
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            XmlObject obj = XmlObject.Factory.parse(classLoader.getResourceAsStream("schemaorg_apache_xmlbeans/src/" + name));
            
            schema = builder.buildElement(null, obj.newXMLStreamReader());
            Document schemaDoc = new Document(schema);
            
            String ns = xbeanType.getSchemaType().getNamespaceURI();
            String expr = "//xsd:schema[@targetNamespace='" + ns + "']";

            List nodes = getMatches(schema, expr);
            if (nodes.size() == 0)
            {
                return null;
            }
            
            Element node = (Element) nodes.get(0);
            
            nodes = getMatches(schema, "//xsd:import");
            for (int i = 0; i < nodes.size(); i++)
            {
                Element imp = (Element) nodes.get(i);
                
                String importedNs = imp.getAttributeValue("namespace");
                
                // TODO: How do we make sure this is imported???
                
                imp.detach();
            }
            
            return node;
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException("Couldn't load schema.", e);
        }
    }

    private List getMatches(Object doc, String xpath)
    {
        try
        {
            XPath path = new YOMXPath(xpath);
            path.addNamespace("xsd", SoapConstants.XSD);
            path.addNamespace("s", SoapConstants.XSD);
            List result = path.selectNodes(doc);
            return result;
        }
        catch(JaxenException e)
        {
            throw new XFireRuntimeException("Error evaluating xpath " + xpath, e);
        }
    }
}
