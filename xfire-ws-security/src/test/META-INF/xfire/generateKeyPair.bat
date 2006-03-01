rem @echo off
echo alias %1
echo keypass %2
echo KeyStorePass %3


keytool -genkey -alias %1 -keypass %2 -keystore privatestore.jks -storepass %3  -dname "cn=%1" -keyalg RSA
keytool -selfcert -alias %1 -keystore privatestore.jks -storepass %3 -keypass %2
keytool -export -alias %1 -file key.rsa -keystore privatestore.jks -storepass %3
keytool -import -alias %1 -file key.rsa -keystore publicstore.jks -storepass %3

