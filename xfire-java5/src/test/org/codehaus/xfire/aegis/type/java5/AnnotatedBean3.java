package org.codehaus.xfire.aegis.type.java5;

public class AnnotatedBean3
{
    private String elementProperty;
    private String attributeProperty;
    private String ignoredProperty;
    
    @XmlAttribute(name="attribute")
    public String getAttributeProperty()
    {
        return attributeProperty;
    }
    
    public void setAttributeProperty(String attributeProperty)
    {
        this.attributeProperty = attributeProperty;
    }
}
