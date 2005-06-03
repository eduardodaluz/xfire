package org.codehaus.xfire.addressing;

import org.codehaus.yom.Element;

public interface AddressingHeadersFactory
{
    AddressingHeaders createHeaders(Element root);
    
    EndpointReference createEPR(Element root);
    
    boolean hasHeaders(Element root);
}
