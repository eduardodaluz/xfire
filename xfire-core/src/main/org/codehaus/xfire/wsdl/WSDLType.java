package org.codehaus.xfire.wsdl;

import java.util.Set;

import org.dom4j.Element;
import org.dom4j.QName;

/**
 * 
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface WSDLType
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
     * The schema type that this WSDLType represents.
     * 
     * @return
     */
    QName getSchemaType();
}
