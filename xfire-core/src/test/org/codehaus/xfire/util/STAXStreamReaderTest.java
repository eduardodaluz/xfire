package org.codehaus.xfire.util;

import org.codehaus.xfire.AbstractXFireTest;
import org.dom4j.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 18, 2004
 */
public class STAXStreamReaderTest
    extends AbstractXFireTest
{
    public void testStreamReader()
        throws Exception
    {
        STAXStreamReader r = new STAXStreamReader();
        
        Document doc =
            r.readDocument( getResourceAsStream("/org/codehaus/xfire/echo11.xml") );

        System.out.println( doc.asXML() );
    }
}
