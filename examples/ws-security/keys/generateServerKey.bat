call generateKeyPair.bat serverAlias aliaspass serverStore.jks keystorePass serverKey.rsa
call generateKeyPair.bat client-344-839  client344Password  clientStore.jks keystorePass clientKey.rsa
keytool -import -alias serverAlias -file serverKey.rsa -keystore clientStore.jks -storepass keystorePass -noprompt
keytool -import -alias client-344-839 -file clientKey.rsa -keystore serverStore.jks -storepass keystorePass -noprompt