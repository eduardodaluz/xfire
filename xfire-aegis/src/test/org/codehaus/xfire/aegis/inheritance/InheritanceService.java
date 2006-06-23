/**
 * 
 */
package org.codehaus.xfire.aegis.inheritance;

public class InheritanceService
{
    public BaseUser getEmployee()
    {
        Employee e = new Employee();
        e.setDivision("foo");
        e.setName("Dan D. Man");
        return e;
    }

    public void receiveUser(BaseUser user)
    {
        InheritancePOJOTest.assertTrue(user instanceof Employee);
    }
}