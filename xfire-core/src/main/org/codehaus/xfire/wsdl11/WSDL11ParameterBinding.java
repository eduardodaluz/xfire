package org.codehaus.xfire.wsdl11;

import javax.wsdl.Message;

import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.wsdl11.builder.AbstractWSDL;

public interface WSDL11ParameterBinding
{
    public String getStyle();
    
    public String getUse();

    public void createInputParts(ServiceEndpoint service, 
                                 AbstractWSDL wsdl,
                                 Message req, 
                                 OperationInfo op);
    
    public void createOutputParts(ServiceEndpoint service, 
                                  AbstractWSDL wsdl,
                                  Message req, 
                                  OperationInfo op);
}
