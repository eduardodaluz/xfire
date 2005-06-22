package org.codehaus.xfire.wsdl11;

import javax.wsdl.Message;

import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;

public interface WSDL11ParameterBinding
{
    public String getStyle();
    
    public String getUse();

    public void createInputParts(WSDLBuilder builder,
                                 Message req, 
                                 OperationInfo op);
    
    public void createOutputParts(WSDLBuilder builder,
                                  Message req, 
                                  OperationInfo op);
}
