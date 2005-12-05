package org.codehaus.xfire.addressing;

import org.jdom.Element;

public interface AddressingHeadersFactory
{
    AddressingHeaders createHeaders(Element root);
    
 
    
    EndpointReference createEPR(Element root);
    
    boolean hasHeaders(Element root);
    
    void writeHeaders(Element root, AddressingHeaders headers);
    
    void writeEPR(Element root, EndpointReference epr);

    String getAnonymousUri();
    
    String getNoneUri();
}
