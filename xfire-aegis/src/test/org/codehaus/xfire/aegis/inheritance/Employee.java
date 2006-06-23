/**
 * 
 */
package org.codehaus.xfire.aegis.inheritance;

// @XmlType(namespace="urn:xfire:inheritance")
public class Employee
    extends BaseUser
{
    private String division;

    public String getDivision()
    {
        return division;
    }

    public void setDivision(String division)
    {
        this.division = division;
    }

}