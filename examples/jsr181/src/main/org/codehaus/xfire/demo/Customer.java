package org.codehaus.xfire.demo;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class Customer
{
    private String name;
    private String email;
    private String phone;
    
    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getPhone()
    {
        return phone;
    }
    public void setPhone(String phone)
    {
        this.phone = phone;
    }
}
