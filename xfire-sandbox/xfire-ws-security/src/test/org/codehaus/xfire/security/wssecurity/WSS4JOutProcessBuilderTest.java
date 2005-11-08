package org.codehaus.xfire.security.wssecurity;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class WSS4JOutProcessBuilderTest
    extends TestCase
{

    public WSS4JOutProcessBuilderTest(String arg0)
    {
        super(arg0);
    }

    public void testBuilder()
        throws Exception
    {
        WSS4JOutSecurityProcessor processor = new WSS4JOutSecurityProcessor();
        WSS4JOutProcessorBuilder builder = new WSS4JOutProcessorBuilder();
        builder.build(processor);
        assertNotNull(processor.getCrypto());
        assertNotNull(processor.getAlias());
    }

}
