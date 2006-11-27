#rem @echo off
#echo alias %1
#echo keypass %2
#echo KeyStorePass %3


keytool -genkey -alias myAlias -keypass myAliasPassword -keystore privatestore.jks -storepass keyStorePassword  -dname "cn=myAlias" -keyalg RSA -validity 356
keytool -selfcert -alias myAlias -keystore privatestore.jks -storepass keyStorePassword -keypass myAliasPassword
keytool -export -alias myAlias -file key.rsa -keystore privatestore.jks -storepass keyStorePassword
keytool -import -alias myAlias -file key.rsa -keystore publicstore.jks -storepass keyStorePassword

