package org.codehaus.xfire.service.binding;

import javax.xml.namespace.QName;

import org.codehaus.xfire.soap.Soap11;

public class RPCEncodedBindingTest
        extends AbstractSOAPBindingTest
{
    protected SOAPBinding getSOAPBinding(QName name)
    {
        return new RPCEncodedBinding(name, Soap11.getInstance());
    }
}
