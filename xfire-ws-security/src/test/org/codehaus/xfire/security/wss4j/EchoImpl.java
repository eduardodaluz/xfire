/*
 * Created on Dec 22, 2005
 */
package org.codehaus.xfire.security.wss4j;


/**
 * Service that echoes the username and password back to the user in an xml
 * document
 * 
 * @author Brian Bonner
 * 
 */
public class EchoImpl
    implements Echo

{
    public EchoImpl()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.xfire.security.UsenamePasswordEchoService#echo(java.lang.String,
     *      org.codehaus.xfire.MessageContext)
     */
    public String echo(String inDocument)
    {
        // We really don't care what's in the incoming document

//        Document document = new Document();
//        Element echo = new Element("echo");
//        document.addContent(0, echo);
//        Element username = new Element("username");
//        username.setText((String) messageContext
//                .getProperty(SecurityConstants.SECURITY_IN_USER_NAME_CONTEXT_KEY));
//        echo.addContent(username);
//
//        Element password = new Element("password");
//        password.setText((String) messageContext
//                .getProperty(SecurityConstants.SECURITY_IN_USER_PASS_CONTEXT_KEY));
//        echo.addContent(password);
//
//        XMLOutputter outputter = new XMLOutputter();
//        return outputter.outputString(document);
          return inDocument;
    }

}
