package org.codehaus.xfire.fault;

import javax.xml.namespace.QName;

public class AuthenticationFault_Exception
    extends FaultInfoException
{
    private AuthenticationFault faultInfo;

    public AuthenticationFault_Exception(String message, AuthenticationFault faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public AuthenticationFault_Exception(String message, Throwable t, AuthenticationFault faultInfo) {
        super(message, t);
        this.faultInfo = faultInfo;
    }

    public AuthenticationFault getFaultInfo() {
        return faultInfo;
    }

    public static QName getFaultName() {
        return new QName("urn:xfire:authenticate:fault", "AuthenticationFault");
    }
}
