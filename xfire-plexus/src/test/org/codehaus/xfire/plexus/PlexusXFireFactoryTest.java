package org.codehaus.xfire.plexus;

import junit.framework.TestCase;

import org.codehaus.xfire.XFireFactory;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PlexusXFireFactoryTest
	extends TestCase
{
    public void testFactory() throws Exception
    {
        XFireFactory.registerFactory(PlexusXFireFactory.class, true);
        XFireFactory factory = XFireFactory.newInstance();
        
        assertTrue( factory instanceof PlexusXFireFactory );
    }
}
