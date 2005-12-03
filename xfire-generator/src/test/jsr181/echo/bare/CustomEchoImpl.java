package jsr181.echo.bare;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.codehaus.xfire.test.echo.EchoRequestDocument;
import org.codehaus.xfire.test.echo.EchoResponseDocument;

@WebService(endpointInterface = "jsr181.echo.bare.EchoPortType", serviceName = "Echo", targetNamespace = "http://xfire.codehaus.org/test/echo")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.BARE)
public class CustomEchoImpl extends EchoImpl
{
    @Override
    public EchoResponseDocument echo(EchoRequestDocument echoRequest)
    {
        EchoResponseDocument document = EchoResponseDocument.Factory.newInstance();
        document.setEchoResponse(echoRequest.getEchoRequest());
        return document;
    }
}
