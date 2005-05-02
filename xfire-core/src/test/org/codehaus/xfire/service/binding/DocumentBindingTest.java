package org.codehaus.xfire.service.binding;

import javax.xml.namespace.QName;

import org.codehaus.xfire.soap.Soap11;

public class DocumentBindingTest

        extends AbstractSOAPBindingTest
{

    protected SOAPBinding getSOAPBinding(QName name)
    {
        return new DocumentBinding(name, Soap11.getInstance());
    }


}