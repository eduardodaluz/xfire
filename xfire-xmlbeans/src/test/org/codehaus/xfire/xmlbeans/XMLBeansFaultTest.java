package org.codehaus.xfire.xmlbeans;

import junit.framework.TestCase;

import org.apache.xmlbeans.XmlObject;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XMLBeansFaultTest
	extends TestCase
{
    public void testFault() throws Exception
    {
        XmlObject faultPart = XmlObject.Factory.parse(getClass().getResourceAsStream("soap11fault.xml"));
        
        assertNotNull(faultPart);
        
        XmlBeansFault fault = new XmlBeansFault(faultPart);
        assertEquals("SOAP-ENV:Server", fault.getFaultCode());
        assertEquals("Server Error", fault.getReason());
        assertNotNull(fault.getDetail());
    }
}
