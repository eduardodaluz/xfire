package org.codehaus.xfire.jaxws.services;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.codehaus.xfire.fault.XFireFault;

import services.echo.EchoPortType;

@WebService(serviceName = "Echo", targetNamespace = "urn:echo:wrapped", endpointInterface = "services.echo.EchoPortType")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public class EchoImpl
    implements EchoPortType
{


    public String echo(String text)
        throws XFireFault
    {
        return text;
    }

}
