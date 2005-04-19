package org.codehaus.xfire.xmlbeans;

import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaType;
import org.codehaus.yom.Element;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 13, 2004
 */
public class XMLBeansType 
    implements org.codehaus.xfire.wsdl.SchemaType
{
    private QName schemaType;

    public XMLBeansType()
    {
    }
    
    public XMLBeansType(SchemaType schemaType)
    {
        this.schemaType = schemaType.getDocumentElementName();
    }

    public void writeSchema(Element element)
    {
        // todo:
    }
    
    public boolean isComplex()
    {
        return true;
    }

    public boolean isAbstract()
    {
        return false;
    }

    public Set getDependencies()
    {
        return null;
    }

    public QName getSchemaType()
    {
        return schemaType;
    }
}
