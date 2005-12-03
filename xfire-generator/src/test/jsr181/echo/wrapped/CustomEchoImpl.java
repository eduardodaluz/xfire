package jsr181.echo.wrapped;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

@WebService(endpointInterface = "jsr181.echo.wrapped.EchoPortType", serviceName = "Echo", targetNamespace = "http://xfire.codehaus.org/test/echo")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public class CustomEchoImpl extends EchoImpl
{
    @Override
    public String echo(String text)
    {
        return text;
    }
}
