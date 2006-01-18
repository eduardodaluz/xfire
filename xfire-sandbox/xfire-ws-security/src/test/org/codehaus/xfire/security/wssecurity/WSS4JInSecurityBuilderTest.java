package org.codehaus.xfire.security.wssecurity;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class WSS4JInSecurityBuilderTest
    extends TestCase
{

    public WSS4JInSecurityBuilderTest(String arg0)
    {
        super(arg0);

    }

    public void testBuilder()
        throws Exception
    {

        WSS4JInSecurityProcessor processor = new WSS4JInSecurityProcessor();
        WSS4JInProcessorBuilder builder = new WSS4JInProcessorBuilder();
        builder.build(processor);
        assertNotNull(processor.getCrypto());
        assertNotNull(processor.getPasswords());

    }
}
