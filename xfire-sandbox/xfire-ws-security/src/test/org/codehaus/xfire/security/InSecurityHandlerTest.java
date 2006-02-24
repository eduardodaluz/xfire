package org.codehaus.xfire.security;

import org.codehaus.xfire.security.handlers.InSecurityHandler;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

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
