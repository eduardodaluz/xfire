package org.codehaus.xfire.aegis.type.java5;

import java.util.ArrayList;
import java.util.Collection;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.codehaus.xfire.aegis.Holder;

@WebService
public class DualOutService
{
    @WebMethod
    public String getValues(@WebParam(mode=WebParam.Mode.OUT) Holder out2)
    {
        out2.setValue("hi");
        return "hi";
    }
    
    @WebMethod
    public Collection<String> getStrings()
    {
        return new ArrayList<String>();
    }
}
