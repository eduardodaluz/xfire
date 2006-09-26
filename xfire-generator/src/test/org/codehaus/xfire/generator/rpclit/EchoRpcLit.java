
package org.codehaus.xfire.generator.rpclit;

import javax.jws.WebService;

import org.codehaus.xfire.generator.rpclit.EchoPortType;

@WebService(serviceName = "Echo", targetNamespace = "http://xfire.codehaus.org/test/echo", endpointInterface = "org.codehaus.xfire.generator.rpclit.EchoPortType")
public class EchoRpcLit
    implements EchoPortType
{
    public String echo(String text) {
        return text;
    }
}
