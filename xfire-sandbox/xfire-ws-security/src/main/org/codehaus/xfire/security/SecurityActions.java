package org.codehaus.xfire.security;

public interface SecurityActions
{
    String AC_ENCRYPT = "encrypt";

    String AC_SIGNATURE = "signature";

    String AC_USERTOKEN = "usertoken";

    String AC_TIMESTAMP = "timestamp";

    String[] ALL_ACTIONS = { AC_ENCRYPT, AC_SIGNATURE, AC_USERTOKEN, AC_TIMESTAMP, };
}
