package org.codehaus.xfire.wsdl;

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

import org.codehaus.xfire.SOAPConstants;
import org.codehaus.xfire.util.NamespaceHelper;
import org.codehaus.xfire.service.Service;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * AbstractWSDL
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public abstract class AbstractWSDL
    implements WSDL
{
    private Definition def;
    private String targetNamespace;
    private Service service;
    private Document wsdlDocument;
    private Map dependencies;
    private Element schemaTypes;
    private Map typeMap;

    /*-------------------------------------------------
     * Namespace and QName definitions for easy access.
     *-------------------------------------------------*/

    protected Namespace xsdNs = new Namespace( "xsd", SOAPConstants.XSD );
    
    protected org.dom4j.QName schemaQ = new org.dom4j.QName("schema", xsdNs);

    protected org.dom4j.QName elementQ = new org.dom4j.QName("element", xsdNs);

    /** A QName for elements which hold the schema that a WSDL type writes out. */
    protected org.dom4j.QName xfireTypeQ = org.dom4j.QName.get( "xfire", "http://xfire.codehaus.org" );
    
    public AbstractWSDL( Service service )
        throws WSDLException
    {
        dependencies = new HashMap();
        this.service = service;
        
        setDefinition( WSDLFactory.newInstance().newDefinition() );
        getDefinition().setTargetNamespace( service.getDefaultNamespace() );
        
        Document paramDoc = DocumentFactory.getInstance().createDocument();
        setSchemaTypes( paramDoc.addElement( "root" ) );
        getSchemaTypes().add(xsdNs);
        
        addNamespace( "soap", service.getSoapVersion() );
        addNamespace( "xsd", SOAPConstants.XSD );
        addNamespace( "wsdl", WSDL11_NS );
        addNamespace( "wsdlsoap", WSDL11_SOAP_NS );
        addNamespace( "tns", service.getDefaultNamespace() );

        typeMap = new HashMap();
    }
    
    protected void writeDocument() 
        throws WSDLException
    {
        org.w3c.dom.Document doc = WSDLFactory.newInstance().newWSDLWriter().getDocument( def );
        
        wsdlDocument = new DOMReader().read(doc);
        wsdlDocument.getRootElement().addNamespace( "tns", service.getDefaultNamespace() );
        
        writeComplexTypes();
    }
    
    protected void writeComplexTypes()
        throws WSDLException
    {
        Element rootEl = getDocument().getRootElement();
        
        Namespace wsdlNs = rootEl.getNamespaceForURI( WSDL11_NS );
        org.dom4j.QName typeQ = new org.dom4j.QName("types", wsdlNs);   
        Element types = rootEl.addElement( typeQ );
    
        // put the types as the first thing in the document
        List list = rootEl.content();
        list.remove( types );
        list.set(0, types);

        for ( Iterator nsItr = typeMap.keySet().iterator(); nsItr.hasNext(); )
        {
            String schemaNs = (String) nsItr.next();
    
            Element schema = types.addElement( schemaQ );
            schema.addAttribute( "targetNamespace", schemaNs );
            schema.addAttribute( "elementFormDefault", "qualified" );
            schema.addAttribute( "attributeFormDefault", "qualified" );
            
            writeSchemaForNamespace( schema, schemaNs );
        }
        
    }

    public void addDependency(WSDLType type)
    {
        if ( !type.isComplex() )
            return;
            
        if ( !dependencies.containsKey( type.getSchemaType() ) )
        {
            dependencies.put( type.getSchemaType(), type );
            
            Element e = createSchemaType( type.getSchemaType().getNamespaceURI() );
            type.writeSchema( e );
        }

        Set deps = type.getDependencies();
        
        if ( deps != null )
        {
            for ( Iterator itr = deps.iterator(); itr.hasNext(); )
            {
                addDependency( (WSDLType) itr.next() );
            }
        }
    }
    
    /**
     * Write the schema types for a particular namespace.
     * 
     * @param schema The schema definition for this namespace. 
     * Attach the types to this.
     * @param schemaNs The namespace to write the types for.
     */
	protected void writeSchemaForNamespace(Element schema, String schemaNs)
	{
        List types = (List) typeMap.get(schemaNs);
        
        if ( types != null )
        {
        	for ( Iterator itr = types.iterator(); itr.hasNext(); )
            {
                Element el = (Element) itr.next();
                
                for ( Iterator eitr = el.elements().iterator(); eitr.hasNext(); )
                {
                     Node n = ((Element) eitr.next()).detach();
                     schema.add(n);
                }

                Namespace ns = NamespaceHelper.getNamespace(el, schemaNs);

                getDocument().getRootElement().add( ns );
            }
        }
	}

	/**
     * @see org.codehaus.xfire.wsdl.WSDL#write(java.io.OutputStream)
     */
    public void write(OutputStream out) throws IOException
    {
        XMLWriter writer = new XMLWriter( OutputFormat.createCompactFormat() );
        writer.setOutputStream( out );
        writer.write( getDocument() );
        writer.flush();
    }
    
    public void addNamespace(String prefix, String uri)
    {
        def.addNamespace( prefix, uri );
        schemaTypes.addNamespace( prefix, uri );
    }
    
    public Namespace getNamespace( String uri )
    {
        return NamespaceHelper.getNamespace( schemaTypes, uri );
    }
    
    /**
     * @see org.codehaus.xfire.wsdl.WSDL#getDocument()
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
    
    /**
     * Create a shcema type element and store it to be written later on.
     * 
     * @param namespace The namespace to create the type in.
     * @return
     */
    public Element createSchemaType( String namespace )
    {
        Element e = getSchemaTypes().addElement( xfireTypeQ );
        
        List types = (List) typeMap.get( namespace );
        if ( types == null )
        {
        	types = new ArrayList();
            typeMap.put( namespace, types );
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
