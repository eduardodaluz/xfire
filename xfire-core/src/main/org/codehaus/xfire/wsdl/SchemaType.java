package org.codehaus.xfire.wsdl;

import java.util.Set;

import javax.xml.namespace.QName;

import org.dom4j.Element;

/**
 * An XSD type.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface SchemaType
{
    /**
     * Whether or not this a complex type.  If true then
     * the schema for this type is written out.
     * 
     * @return
     */
	boolean isComplex();
    
    /**
     * The types that this type references.
     * 
     * @return
     */
    Set getDependencies();
    
    /**
     * Write the type schema (if complex) to the element.
     * 
     * @param element
     */
    void writeSchema( Element element );
    
    /**
     * The schema type that this SchemaType represents.
     * 
     * @return
     */
    QName getSchemaType();
}
