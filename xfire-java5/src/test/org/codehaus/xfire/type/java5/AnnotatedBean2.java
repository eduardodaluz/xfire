package org.codehaus.xfire.type.java5;

public class AnnotatedBean2
{
    private String elementProperty;
    private String attributeProperty;
    private String bogusProperty;
    
    @XmlAttribute(name="attribute")
    public String getAttributeProperty()
    {
        return attributeProperty;
    }
    
    public void setAttributeProperty(String attributeProperty)
    {
        this.attributeProperty = attributeProperty;
    }

    @XmlElement(name="element")
    public String getElementProperty()
    {
        return elementProperty;
    }
    
    public void setElementProperty(String elementProperty)
    {
        this.elementProperty = elementProperty;
    }
}
