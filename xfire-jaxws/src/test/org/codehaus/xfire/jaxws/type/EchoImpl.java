package org.codehaus.xfire.jaxws.type;

import javax.jws.WebService;
import javax.xml.ws.Holder;

import services.headerout.EchoPortType;
import echo.wrapped.Echo;
import echo.wrapped.EchoResponse;


@WebService(serviceName = "Echo", targetNamespace = "urn:echo:wrapped", endpointInterface = "services.headerout.EchoPortType")
public class EchoImpl
    implements EchoPortType
{

    public EchoResponse echo(Echo echo, Echo echo2, Holder<EchoResponse> echoResponse, Holder<EchoResponse> echoResponse2)
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
