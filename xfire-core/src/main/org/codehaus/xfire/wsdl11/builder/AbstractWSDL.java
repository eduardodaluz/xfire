package org.codehaus.xfire.wsdl11.builder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.util.NamespaceHelper;
import org.codehaus.xfire.wsdl.SchemaType;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.yom.Attribute;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;
import org.codehaus.yom.Node;
import org.codehaus.yom.Serializer;
import org.codehaus.yom.converters.DOMConverter;

/**
 * AbstractWSDL
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public abstract class AbstractWSDL
    implements WSDLWriter
{
    private Definition def;

    private String targetNamespace;

    private ServiceEndpoint service;

    private Document wsdlDocument;

    private Map dependencies;

    private Element schemaTypes;

    private Map typeMap;
    
    private WSDLBuilderInfo info;

    /*-------------------------------------------------
     * Namespace and QName definitions for easy access.
     *-------------------------------------------------*/

    public final static String schemaQ = SoapConstants.XSD_PREFIX + ":" + "schema";

    public final static String elementQ = SoapConstants.XSD_PREFIX + ":" + "element";

    public final static String complexQ = SoapConstants.XSD_PREFIX + ":" + "complexType";

    public final static String sequenceQ = SoapConstants.XSD_PREFIX + ":" + "sequence";

    public AbstractWSDL(ServiceEndpoint service) throws WSDLException
    {
        dependencies = new HashMap();
        this.service = service;
        this.info = (WSDLBuilderInfo) service.getProperty(WSDLBuilderInfo.KEY);

        if (info == null)
            info = new WSDLBuilderInfo(service);

        setDefinition(WSDLFactory.newInstance().newDefinition());
        getDefinition().setTargetNamespace(info.getTargetNamespace());

        Element root = new Element("root");
        Document paramDoc = new Document(root);
        setSchemaTypes(root);
        root.addNamespaceDeclaration(SoapConstants.XSD_PREFIX, SoapConstants.XSD);

        addNamespace("soap", service.getSoapVersion().getNamespace());
        addNamespace("soapenc", service.getSoapVersion().getSoapEncodingStyle());
        addNamespace("xsd", SoapConstants.XSD);
        addNamespace("wsdl", WSDL11_NS);
        addNamespace("wsdlsoap", WSDL11_SOAP_NS);
        addNamespace("tns", info.getTargetNamespace());

        typeMap = new HashMap();
    }

    protected void writeDocument()
        throws WSDLException
    {
        org.w3c.dom.Document doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(def);

        wsdlDocument = DOMConverter.convert(doc);

        writeComplexTypes();
    }

    protected void writeComplexTypes()
        throws WSDLException
    {
        Element rootEl = getDocument().getRootElement();

        Element types = new Element("wsdl:types", WSDL11_NS);
        rootEl.insertChild(types, 0);

        for (Iterator nsItr = typeMap.keySet().iterator(); nsItr.hasNext();)
        {
            String schemaNs = (String) nsItr.next();
            List schemaTypes = (List) typeMap.get(schemaNs);

            if (schemaTypes.size() > 0)
            {
                Element schema = new Element(schemaQ, SoapConstants.XSD);
                types.appendChild(schema);

                schema.addAttribute(new Attribute("targetNamespace", schemaNs));
                schema.addAttribute(new Attribute("elementFormDefault", "qualified"));
                schema.addAttribute(new Attribute("attributeFormDefault", "qualified"));
            
                writeSchemaForNamespace(schema, schemaNs, schemaTypes);
            }
        }

    }

    public void addDependency(SchemaType type)
    {
        if (!type.isComplex())
        {
            return;
        }
        
        if (!dependencies.containsKey(type.getSchemaType()))
        {
            dependencies.put(type.getSchemaType(), type);

            Element e = createSchemaType(type.getSchemaType().getNamespaceURI());
            type.writeSchema(e);
        }

        Set deps = type.getDependencies();

        if (deps != null)
        {
            for (Iterator itr = deps.iterator(); itr.hasNext();)
            {
                addDependency((SchemaType) itr.next());
            }
        }
    }

    /**
     * Write the schema types for a particular namespace.
     * 
     * @param schema
     *            The schema definition for this namespace. Attach the types to
     *            this.
     * @param schemaNs
     *            The namespace to write the types for.
     */
    protected void writeSchemaForNamespace(Element schema, String schemaNs, List types)
    {       
        for (Iterator itr = types.iterator(); itr.hasNext();)
        {
            Element el = (Element) itr.next();

            for (int i = 0; i < el.getChildCount(); i++)
            {
                Node n = el.getChild(i);
                n.detach();
                schema.appendChild(n);
            }

            String prefix = NamespaceHelper.getUniquePrefix(el, schemaNs);
            String declaredUri = getDocument().getRootElement().getNamespaceURI(prefix);
            if (declaredUri == null)
                getDocument().getRootElement().addNamespaceDeclaration(prefix, schemaNs);
        }
    }

    /**
     * @see org.codehaus.xfire.wsdl.WSDLWriter#write(java.io.OutputStream)
     */
    public void write(OutputStream out)
        throws IOException
    {
        Serializer writer = new Serializer(out);
        writer.write(getDocument());
        writer.flush();
    }

    public void addNamespace(String prefix, String uri)
    {
        def.addNamespace(prefix, uri);

        String declaredUri = schemaTypes.getNamespaceURI(prefix);
        if (declaredUri == null)
        {
            schemaTypes.addNamespaceDeclaration(prefix, uri);
        }
        else if (!declaredUri.equals(uri))
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

    public ServiceEndpoint getService()
    {
        return service;
    }

    public void setService(ServiceEndpoint service)
    {
        this.service = service;
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
        Element e = new Element("xfiretemp");
        getSchemaTypes().appendChild(e);

        List types = (List) typeMap.get(namespace);
        if (types == null)
        {
            types = new ArrayList();
            typeMap.put(namespace, types);
        }

        types.add(e);

        return e;
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
