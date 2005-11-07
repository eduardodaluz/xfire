package org.codehaus.xfire.xmlbeans;

import java.util.ArrayList;
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
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.codehaus.xfire.util.stax.FragmentStreamReader;
import org.codehaus.xfire.wsdl11.WSDL11ParameterBinding;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

public class XmlBeansWSDLBuilder
    extends WSDLBuilder
{
    private final static StaxBuilder builder = new StaxBuilder();
    private static Map schemas = new HashMap();
    
    private List importedSchemas = new ArrayList();
    
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
                String ns = xbeanType.getSchemaType().getNamespaceURI();
                if (!importedSchemas.contains(schema))
                {
                    schema.detach();
                    setSchema(ns, schema);
                    
                    importedSchemas.add(schema);
                }
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
            
            schema = builder.build(new FragmentStreamReader(obj.newXMLStreamReader())).getRootElement();
            
            schemas.put(name, schema);
            
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
                
                Attribute schemaLoc = imp.getAttribute("schemaLocation");
                
                // TODO: How do we make sure this is imported???
                
                if (schemaLoc != null)
                    schemaLoc.detach();
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
            XPath path = XPath.newInstance(xpath);
            path.addNamespace("xsd", SoapConstants.XSD);
            path.addNamespace("s", SoapConstants.XSD);
            List result = path.selectNodes(doc);
            return result;
        }
        catch(JDOMException e)
        {
            throw new XFireRuntimeException("Error evaluating xpath " + xpath, e);
        }
    }
}
