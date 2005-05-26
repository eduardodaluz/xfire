package org.codehaus.xfire.addressing;

import java.util.List;

import javax.xml.namespace.QName;

import org.codehaus.yom.Element;

public interface EndpointReference 
{
    String getAddress();

    QName getInterfaceName();

    QName getServiceName();

    String getEndpointName();

    Element getPolicies();
    
    /**
     * Other non-standard WS-Adressing elements which should be included in the
     * endpoint reference.
     * 
     * @return
     */
    List getAnyContent();
}