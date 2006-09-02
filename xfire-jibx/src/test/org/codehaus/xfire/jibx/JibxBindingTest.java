package org.codehaus.xfire.jibx;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.BeanInvoker;
import org.jdom.Document;

public class JibxBindingTest
    extends AbstractXFireAegisTest
{
    public void testBinding() throws Exception
    {
        Service service = new JibxServiceFactory().create(AccountService.class, null, "http://xfire.codehaus.org/", null);
        service.setInvoker(new BeanInvoker(new AccountServiceImpl()));
        getServiceRegistry().register(service);
        
        List schemas = new ArrayList();
        schemas.add("org/codehaus/xfire/jibx/account.xsd");
        service.setProperty(ObjectServiceFactory.SCHEMAS, schemas);
        Document response = invokeService("AccountService", "/org/codehaus/xfire/jibx/getAccountStatus.xml");
        addNamespace("a", "http://xfire.codehaus.org/jibx");
        //assertValid("//s:Body/a:getAccountStatusResponse/a:AccountInfo/a:amount[text()='0']", response);
        
        Document wsdl = getWSDLDocument("AccountService");
        printNode(wsdl);
    }
}
