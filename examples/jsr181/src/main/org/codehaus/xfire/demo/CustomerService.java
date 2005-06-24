package org.codehaus.xfire.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
@WebService
public class CustomerService
{
    private List<Customer> customers = new ArrayList<Customer>();
    
    public CustomerService()
    {
    }

    @WebMethod
    @WebResult(name="Customers")
    public Collection<Customer> getCustomers(@WebParam(name="UserToken", header=true) UserToken auth)
    {
        authorize(auth);
        
        return customers;
    }
    
    private void authorize(UserToken auth)
    {
        System.out.println(auth.getUsername());
        System.out.println(auth.getPassword());
    }

    @WebMethod
    public String addCustomer(@WebParam(name="UserToken", header=true) UserToken auth, 
                              @WebParam(name="customer") Customer customer)
    {
        authorize(auth);
        
        customers.add(customer);
        
        return "";
    }
}
