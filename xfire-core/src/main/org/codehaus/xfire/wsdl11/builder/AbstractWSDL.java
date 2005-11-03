package org.codehaus.xfire.wsdl11.builder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.stream.XMLStreamException;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.codehaus.xfire.util.NamespaceHelper;
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.codehaus.xfire.wsdl.SchemaType;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;


/**
 * Provides schema functionality for a WSDLBuilder.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public abstract class AbstractWSDL
    implements WSDLWriter
{
    private static final StaxBuilder builder = new StaxBuilder();
    
    private Definition def;

    private Service service;

    private Document wsdlDocument;

    private Map dependencies = new HashMap();
    
    private Map namespaceImports = new HashMap();
    
    private Element schemaTypes;

    private Map typeMap;
    
    private WSDLBuilderInfo info;

    private boolean schemaLocationRemoved = true;
    
    /*-------------------------------------------------
     * Namespace and QName definitions for easy access.
     *-------------------------------------------------*/

    public final static Namespace XSD_NS = Namespace.getNamespace(SoapConstants.XSD_PREFIX, 
                                                                  SoapConstants.XSD);

    public AbstractWSDL(Service service) throws WSDLException
    {
        this.service = service;
        this.info = (WSDLBuilderInfo) service.getProperty(WSDLBuilderInfo.KEY);

        if (info == null)
            info = new WSDLBuilderInfo(service);

        setDefinition(WSDLFactory.newInstance().newDefinition());
        getDefinition().setTargetNamespace(info.getTargetNamespace());

        Element root = new Element("types", "wsdl", WSDL11_NS);
        setSchemaTypes(root);
        root.addNamespaceDeclaration(Namespace.getNamespace(SoapConstants.XSD_PREFIX, SoapConstants.XSD));

        addNamespace("soap", Soap11.getInstance().getNamespace());
        addNamespace("soapenc", Soap11.getInstance().getSoapEncodingStyle());
        addNamespace("xsd", SoapConstants.XSD);
        addNamespace("wsdl", WSDL11_NS);
        addNamespace("wsdlsoap", WSDL11_SOAP_NS);
        addNamespace("tns", info.getTargetNamespace());

        typeMap = new HashMap();
    }

    protected void writeDocument()
        throws WSDLException
    {
        writeImports();
        
        org.w3c.dom.Document doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(def);

        wsdlDocument = new DOMBuilder().build(doc);

        writeComplexTypes();
    }

    /**
     * Write xs:import elements for each schema.
     */
    protected void writeImports()
    {
        for (Iterator itr = namespaceImports.entrySet().iterator(); itr.hasNext();)
        {
            Map.Entry entry = (Map.Entry) itr.next();
            
            String uri = (String) entry.getKey();
            Set imports = (Set) entry.getValue();
            
            Element schema = createSchemaType(uri);
            
            for (Iterator importItr = imports.iterator(); importItr.hasNext();)
            {
                String ns = (String) importItr.next();
                if (!ns.equals(SoapConstants.XSD) && !hasImport(schema, ns))
                {
                    Element importEl = new Element("import", XSD_NS);
                    importEl.setAttribute(new Attribute("namespace", ns));
                    
                    schema.addContent(0, importEl);
                }
            }
        }
    }

    public boolean hasImport(Element schema, String ns)
    {
        List children = schema.getChildren("import", Namespace.getNamespace(SoapConstants.XSD));
        
        for (int i = 0; i < children.size(); i++)
        {
            Element importEl = (Element) children.get(i);
            String value = importEl.getAttributeValue("namespace");
            
            if (value != null && value.equals(ns)) return true;
        }
        
        return false;
    }

    protected void writeComplexTypes()
        throws WSDLException
    {
        Element rootEl = getDocument().getRootElement();

        if (schemaTypes.getContentSize() > 0)
        {
            schemaTypes.detach();
            rootEl.addContent(0, schemaTypes);
        }
    }

    public void addDependency(SchemaType type)
    {
        if (!type.isComplex())
        {
            return;
        }
        
        if (!hasDependency(type))
        {
            dependencies.put(type.getSchemaType(), type);

            Element e = createSchemaType(type.getSchemaType().getNamespaceURI());
            type.writeSchema(e);
            
            Set deps = type.getDependencies();

            if (deps != null)
            {
                for (Iterator itr = deps.iterator(); itr.hasNext();)
                {
                    SchemaType child = (SchemaType) itr.next();
                    addDependency(child);
                    addNamespaceImport(type.getSchemaType().getNamespaceURI(), 
                                       child.getSchemaType().getNamespaceURI());
                }
            }
        }
    }

    protected boolean hasDependency(SchemaType type)
    {
        return dependencies.containsKey(type.getSchemaType());
    }

    /**
     * Adds an import to another namespace. 
     * @param uri The namespace to import into.
     * @param imported The namespace to import.
     */
    public void addNamespaceImport(String uri, String imported)
    {
        if (uri.equals(imported)) return;
        
        Set imports = (Set) namespaceImports.get(uri);
        
        if (imports == null)
        {
            imports = new HashSet();
            namespaceImports.put(uri, imports);
        }
        
        imports.add(imported);
    }
    
    /**
     * @see org.codehaus.xfire.wsdl.WSDLWriter#write(java.io.OutputStream)
     */
    public void write(OutputStream out)
        throws IOException
    {
        XMLOutputter writer = new XMLOutputter();
        writer.setFormat(Format.getPrettyFormat());
        writer.output(getDocument(), out);
    }

    public void addNamespace(String prefix, String uri)
    {
        def.addNamespace(prefix, uri);

        Namespace declaredUri = schemaTypes.getNamespace(prefix);
        if (declaredUri == null)
        {
            schemaTypes.addNamespaceDeclaration(Namespace.getNamespace(prefix, uri));
        }
        else if (!declaredUri.getURI().equals(uri))
        {
            throw new XFireRuntimeException("Namespace conflict: " + declaredUri
                    + " was declared but " + uri + " was attempted.");
        }
    }

    public String getNamespacePrefix(String uri)
    {
        return NamespaceHelper.getUniquePrefix(schemaTypes, uri);
    }

    public WSDLBuilderInfo getInfo()
    {
        return info;
    }

    /**
     * @see org.codehaus.xfire.wsdl.WSDLWriter#getDocument()
     */
    public Document getDocument()
    {
        return wsdlDocument;
    }

    public Definition getDefinition()
    {
        return def;
    }

    public void setDefinition(Definition definition)
    {
        this.def = definition;
    }

    public Service getService()
    {
        return service;
    }

    public void setService(Service service)
    {
        this.service = service;
    }

    public String getTargetNamespace()
    {
        return service.getServiceInfo().getName().getNamespaceURI();
    }

    public void addSchemas(List schemaLocations)
    {
        for (Iterator itr = schemaLocations.iterator(); itr.hasNext();)
        {
            addSchema((String) itr.next());
        }
    }
    
    /**
     * Loads a schema off the filesystem or the classpath and adds it to the WSDL types section.
     * 
     * @param location
     */
    public void addSchema(String location)
    {
        // Try loading the file as a file, then on the classpath
        InputStream fileInputStream = null;
        try
        {
            fileInputStream = new FileInputStream(location);
        } 
        catch (FileNotFoundException e)
        {
            fileInputStream = ClassLoaderUtils.getResourceAsStream(location, getClass());
        }
        
        if (fileInputStream == null)
            throw new XFireRuntimeException("Couldnt load schema file: " + location);
        
        // Load in the schema
        Document schema = null;
        try
        {
            schema = builder.build(fileInputStream);
        } 
        catch (XMLStreamException e)
        {
            throw new XFireRuntimeException("Error parsing schema file: " + location, e);
        }
        
        // Remove the schemaLocation elements
        if (isSchemaLocationRemoved())
            cleanImports(schema);
        
        String targetNamespace = schema.getRootElement().getAttributeValue("targetNamespace");
        if (targetNamespace != null)
        {
            Element root = schema.getRootElement();
            root.detach();
            setSchema(targetNamespace, root);
        }
        else
        {
            throw new XFireRuntimeException("Could not find target namespace in schema: " + location);
        }
    }

    public boolean isSchemaLocationRemoved()
    {
        return schemaLocationRemoved;
    }

    public void setSchemaLocationRemoved(boolean schemaLocationRemoved)
    {
        this.schemaLocationRemoved = schemaLocationRemoved;
    }

    /**
     * Removes the schemaLocation attribute from an &lt;xsd:import&gt; statement.
     * @param schema
     */
    protected void cleanImports(Document schema)
    {
        List nodes = getMatches(schema, "//xsd:import");
        for (int i = 0; i < nodes.size(); i++)
        {
            Element imp = (Element) nodes.get(i);

            Attribute loc = imp.getAttribute("schemaLocation");

            if (loc != null)
            {
                loc.detach();
            }
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
        catch (JDOMException e)
        {
            throw new XFireRuntimeException("Error evaluating xpath " + xpath, e);
        }
    }
    
    /**
     * Create a shcema type element and store it to be written later on.
     * 
     * @param namespace
     *            The namespace to create the type in.
     * @return
     */
    public Element createSchemaType(String namespace)
    {
        Element e = (Element) typeMap.get(namespace);

        if (e == null)
        {
            e = new Element("schema", XSD_NS);

            e.setAttribute(new Attribute("targetNamespace", namespace));
            e.setAttribute(new Attribute("elementFormDefault", "qualified"));
            e.setAttribute(new Attribute("attributeFormDefault", "qualified"));

            setSchema(namespace, e);
        }

        return e;
    }

    protected boolean hasSchema(String namespace)
    {
        return typeMap.containsKey(namespace);
    }
    
    protected void setSchema(String namespace, Element schema)
    {
        typeMap.put(namespace, schema);
        getSchemaTypes().addContent(schema);
    }

    protected Element getSchemaTypes()
    {
        return schemaTypes;
    }

    protected void setSchemaTypes(Element schemaTypes)
    {
        this.schemaTypes = schemaTypes;
    }
}
