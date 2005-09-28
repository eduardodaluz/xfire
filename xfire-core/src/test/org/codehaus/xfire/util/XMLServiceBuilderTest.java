package org.codehaus.xfire.util;

import java.io.InputStream;

import org.codehaus.xfire.test.AbstractXFireTest;

public class XMLServiceBuilderTest
    extends AbstractXFireTest
{
    public void testFindResourceBundle()
    {
        InputStream configStream = this.getResourceAsStream("/META-INF/xfire/services.xml");
        assertNotNull(configStream);
    }

    public void testServiceProperties()
        throws Exception
    {
        InputStream configStream = this.getResourceAsStream("/META-INF/xfire/services.xml");
        XMLServiceBuilder services = new XMLServiceBuilder(getXFire());
        services.buildServices(configStream);
        assertNotNull(services);

        assertEquals("foo:bar", "bar", getXFire().getServiceRegistry().getService("Echo")
                .getProperty("foo"));
        assertEquals("cheese:baz", "baz", getXFire().getServiceRegistry().getService("Echo")
                .getProperty("cheese"));
    }
}