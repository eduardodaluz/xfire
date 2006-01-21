package org.codehaus.xfire.security.impl;

public interface SecurityProperties
{

    public static final String PROP_KEYSTORE_TYPE = "xfire.security.keystore.type";

    public static final String PROP_KEYSTORE_PASS = "xfire.security.keystore.password";

    public static final String PROP_KEYSTORE_FILE = "xfire.security.keystore.file";

    public static final String PROP_PUBLIC_ALIAS = "xfire.security.public.alias";

    public static final String PROP_CERT_FILE = "xfire.security.cert.file";

    public static final String PROP_ENC_ALG = "xfire.security.encrypt.key.algoritm";

    public static final String PROP_SYM_ALG = "xfire.security.symmetric.key.algoritm";

    public static final String PROP_USER_NAME = "xfire.security.user.name";

    public static final String PROP_USER_PASSWORD = "xfire.security.user.password";

    public static final String PROP_USER_PASSWORD_USE_PLAIN = "xfire.security.user.password.use.plain";

    public static final String PROP_TIME_TO_LIVE = "xfire.security.time.to.live";

    public static final String PROP_ACTIONS = "xfire.security.actions";

    public static final String PROP_PRIVATE_ALIAS = "xfire.security.private.alias";

    public static final String PROP_PRIVATE_PASSWORD = "xfire.security.private.password";
    
    public static final String PROP_PASSWORD_CALLBACK = "xfire.security.password.callback";

    public static final String[] ALL_PROPS = { PROP_USER_PASSWORD_USE_PLAIN, PROP_USER_PASSWORD,
            PROP_USER_NAME, PROP_SYM_ALG, PROP_CERT_FILE, PROP_PUBLIC_ALIAS, PROP_KEYSTORE_FILE,
            PROP_KEYSTORE_PASS, PROP_KEYSTORE_TYPE, PROP_TIME_TO_LIVE, PROP_ACTIONS,
            PROP_PRIVATE_ALIAS, PROP_PRIVATE_PASSWORD, PROP_PASSWORD_CALLBACK,};


}
