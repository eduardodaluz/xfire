package org.codehaus.xfire.service.binding;

import javax.xml.namespace.QName;

import org.codehaus.xfire.soap.Soap11;


public class WrappedBindingTest
        extends AbstractSOAPBindingTest
{
    protected SOAPBinding getSOAPBinding(QName name)
    {
        return new WrappedBinding(name, Soap11.getInstance());
    }
}
