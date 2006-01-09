package org.codehaus.xfire.jaxws.type;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.ws.Holder;

import org.codehaus.xfire.fault.XFireFault;

import services.headerout.EchoPortType;

import echo.wrapped.Echo;
import echo.wrapped.EchoResponse;


@WebService(serviceName = "Echo", targetNamespace = "urn:echo:wrapped", endpointInterface = "services.headerout.EchoPortType")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.BARE)
public class EchoImpl
    implements EchoPortType
{
    public EchoResponse echo(Echo echo, 
                             Echo echo2, 
                             Holder<EchoResponse> echoResponse, 
                             Holder<EchoResponse> echoResponse2)
        throws XFireFault
    {
        EchoResponse response = new EchoResponse();
        response.setText(echo.getText());
        
        EchoResponse response2 = new EchoResponse();
        response2.setText(echo2.getText());
        
        echoResponse.value = response;
        echoResponse2.value = response2;
        
        return response;
    }

}
