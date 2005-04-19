package org.codehaus.xfire.service.binding;

import javax.xml.namespace.QName;

/**
 * A parameter for an operation.
 * <p>
 * A parameter looks up a <code>Type</code> which is used to serialize it. <code>Type</code>s
 * are searched in the following order:
 * <ul>
 * <li>A <code>Type</code> with the schema type of <code>getName()</code>
 * <li>A <code>Type</code> with the schema type of <code>getAbstractType()</code>
 * <li>A <code>Type</code> with the type class of <code>getTypeClass()</code>
 * </ul>
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 16, 2004
 */
public class Parameter
{
    private QName name;
    private QName schemaType;
    private Class typeClass;
    private boolean header = false;
    
    public Parameter()
    {
    }
    
    public Parameter(QName name, Class typeClass)
    {
        this.name = name;
        this.typeClass = typeClass;
    }
    
    /**
     * @return Returns the name.
     */
    public QName getName()
    {
        return name;
    }
    
    /**
     * @param name The name to set.
     */
    public void setName(QName name)
    {
        this.name = name;
    }

    /**
     * The xml schema type of this parameter.
     * @return
     */
    public QName getSchemaType()
    {
        return schemaType;
    }

    public void getSchemaType(QName schemaType)
    {
        this.schemaType = schemaType;
    }

    public Class getTypeClass()
    {
        return typeClass;
    }
    
    public void setTypeClass(Class typeClass)
    {
        this.typeClass = typeClass;
    }
    
    public boolean isHeader()
    {
        return header;
    }
    
    public void setHeader(boolean header)
    {
        this.header = header;
    }

}