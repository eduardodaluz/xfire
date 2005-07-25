package org.codehaus.xfire.fault;

import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.yom.Document;

public class NoServiceFaultTest
    extends AbstractXFireTest
{

    public void testInvoke()
        throws Exception
    {
        Document response = invokeService(null, "/org/codehaus/xfire/echo11.xml");

        assertValid("//s:Fault", response);
    }
}
