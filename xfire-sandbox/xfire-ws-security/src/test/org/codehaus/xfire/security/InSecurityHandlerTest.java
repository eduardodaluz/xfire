package org.codehaus.xfire.security;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageExchange;
import org.codehaus.xfire.security.handlers.InSecurityHandler;
import org.codehaus.xfire.util.STAXUtils;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.util.Dummy;
import org.w3c.dom.Document;

public class InSecurityHandlerTest
    extends MockObjectTestCase
{

    private InSecurityHandler myInSecurityHandler = new InSecurityHandler();

    protected void setUp()
        throws Exception
    {
        // TODO Auto-generated method stub
        super.setUp();
    }

    /*
     * Test method for
     * 'org.codehaus.xfire.security.handlers.InSecurityHandler.invoke(MessageContext)'
     */
    public void testInvoke()
        throws Exception
    {
        Mock mockInSecurityProcessor = mock(InSecurityProcessor.class);
        InSecurityProcessor inSecurityProcessor = (InSecurityProcessor) mockInSecurityProcessor.proxy();
        myInSecurityHandler.setProcessor(inSecurityProcessor);
        
//        MessageContext messageContext = new MessageContext();
//        MessageExchange messageExchange = new MessageExchange(messageContext);
//        InputStream is = this.getClass().getResourceAsStream("sample-wsse-request.xml");
//        XMLStreamReader xmlStreamReader = STAXUtils.createXMLStreamReader(new InputStreamReader(is));
//        InMessage inMessage = new InMessage(xmlStreamReader);
//        messageExchange.setInMessage(inMessage);
//        mockInSecurityProcessor.expects(once()).method("process").with(isA(Document.class)).will(returnValue(Dummy.newDummy(Document.class)));
//        
//        myInSecurityHandler.invoke(messageContext);

    }
}
