package org.codehaus.xfire.jibx;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
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
        
        Document response = invokeService("AccountService", "/org/codehaus/xfire/jibx/getAccountStatus.xml");
        addNamespace("a", "http://xfire.codehaus.org/jibx");
        addNamespace("x", "http://xfire.codehaus.org/");
        assertValid("//s:Body/x:getAccountStatusResponse/a:AccountInfo/a:amount[text()='0']", response);
        
        Document wsdl = getWSDLDocument("AccountService");
        printNode(wsdl);
    }
}
