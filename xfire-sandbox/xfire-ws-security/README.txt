This package contains a very beta implementation of ws-security based on wss4j ( http://ws.apache.org/wss4j/ ) and 
is limited to encryption/decryption functionality.
I didn't integrated it with whole application so i'm not sure if it would work at this moment, but
i will try to do this as soon as possible and also add maven or ant build for this package.
Because I used some parts of my eariler security project which used custom cryptography components,  some methods are unneeded, 
others require a lot of refactoring.
So at this moment it should be considered rather as a proof of concept, then final implementation.
Currently its based on following interfaces and classes :

InSecurityProcessor - responsible for processing of incoming message ( decryption )
InSecurityProcessorBuilder - setup all required data and components of InSecurityProcessor ( like keys, encryption algoritms,etc )

InSecurityResult - result of incomming message processing.Contais data like user/passord from token and decrypted document.
				   user/password probably should be copied from here to MessageContext
				   
OutSecurityProcessor - 	process outgoing message ( currently encryption only )
OutSecurityProcessorBuilder - setup all required data and components of  OutSecurityProcessor  ( like keys, encryption algoritms,etc )


Support for security for application can be added by setting 2 handlers for service :
<inHandlers>
  ...
  <handler class="org.codehaus.xfire.security.wssecurity.WSS4jInSecurityHandler" />
</inHandlers>  
<outHandlers>
  ...
  <handler class="org.codehaus.xfire.security.wssecurity.WSS4JOutSecurityHandler" />
</outHandlers>  

But this will not work till setting handler execution order patch won't be integrated ( this should be soon i hope:)


Configuration of security handlers  is hold in property files META-INF/xfire/insecurity.properties,
 META-INF/xfire/outsecurity.properties in following format :

xfire.security.keystore.password=keystorePass
xfire.security.keystore.file=META-INF/xfire/privatestore.jks
xfire.security.key.alias=alias
xfire.security.key.password=aliaspass


All junit test works for my environment,

Because this implementation use strong cryptography algorithms, so there may be required to download and install 
unlimited cryptography support from java.sun.com 
( http://java.sun.com/products/jce/index-14.html for java 1.4) and security provider from BouncyCastle.org

In case of any questions : tsztelak@gmail.com. Fill free to ping me any time.
