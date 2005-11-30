package org.codehaus.xfire.jaxws.provider;

import javax.xml.transform.Source;
import javax.xml.ws.Provider;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

@WebServiceProvider
@ServiceMode(javax.xml.ws.Service.Mode.PAYLOAD)
public class MessageSourceProvider implements Provider<Source>
{
    public Source invoke(Source req)
    {
        return req;
    }
}
