package org.codehaus.xfire.xmlbeans;

import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.codehaus.yom.Element;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 13, 2004
 */
public class XMLBeansType 
    implements org.codehaus.xfire.wsdl.SchemaType
{
    private SchemaType schemaType;

    public XMLBeansType()
    {
    }
    
    public XMLBeansType(SchemaType schemaType)
    {
        this.schemaType = schemaType;
    }

    public void writeSchema(Element element)
    {
        // todo:
        SchemaTypeSystem typeSys = null;

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
}
