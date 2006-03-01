package org.codehaus.xfire.security.impl;

public interface SecurityProperties
{
	// Keystore type : jks
    static final String PROP_KEYSTORE_TYPE = "xfire.security.keystore.type";
	// Password required to access keystore
    static final String PROP_KEYSTORE_PASS = "xfire.security.keystore.password";
	// Keystore filepath
    static final String PROP_KEYSTORE_FILE = "xfire.security.keystore.file";
	// Alias of public key
    static final String PROP_PUBLIC_ALIAS = "xfire.security.public.alias";
	// Certyficate filepath
    static final String PROP_CERT_FILE = "xfire.security.cert.file";
	// Algorithm used to encrypt encryption key
    static final String PROP_ENC_ALG = "xfire.security.encrypt.key.algoritm";
	// Algorith used to encrypt xml data
    static final String PROP_SYM_ALG = "xfire.security.symmetric.key.algoritm";
	// User name ( for usertoken )
    static final String PROP_USER_NAME = "xfire.security.user.name";
	// User password ( for usertoken )
    static final String PROP_USER_PASSWORD = "xfire.security.user.password";
	// Determine if user's password should be hashed or sent in plain form
    static final String PROP_USER_PASSWORD_USE_PLAIN = "xfire.security.user.password.use.plain";
	// Time to live ( in sec ). After this time message become invalid.
    static final String PROP_TIME_TO_LIVE = "xfire.security.time.to.live";
	// List of actions to take
    static final String PROP_ACTIONS = "xfire.security.actions";
	// Alias for prive key
    static final String PROP_PRIVATE_ALIAS = "xfire.security.private.alias";
	// Private key password
    static final String PROP_PRIVATE_PASSWORD = "xfire.security.private.password";
    // Callback object used to retrive password for given user id
    static final String PROP_PASSWORD_CALLBACK = "xfire.security.password.callback";

    public static final String[] ALL_PROPS = { PROP_USER_PASSWORD_USE_PLAIN, PROP_USER_PASSWORD,
            PROP_USER_NAME, PROP_SYM_ALG, PROP_CERT_FILE, PROP_PUBLIC_ALIAS, PROP_KEYSTORE_FILE,
            PROP_KEYSTORE_PASS, PROP_KEYSTORE_TYPE, PROP_TIME_TO_LIVE, PROP_ACTIONS,
            PROP_PRIVATE_ALIAS, PROP_PRIVATE_PASSWORD, PROP_PASSWORD_CALLBACK,};


}
