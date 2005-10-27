package org.codehaus.xfire.xmpp;

import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.jdom.Document;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

public class XmlPullStreamReaderTest
    extends AbstractXFireTest
{
    public void testParser() throws Exception
    {
        MXParser parser = new MXParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        
        parser.setInput(getClass().getResourceAsStream("echo.xml"), null);
        
        XmlPullStreamReader reader = new XmlPullStreamReader(parser);
        
        StaxBuilder builder = new StaxBuilder();
        Document doc = builder.build(reader);
        
        printNode(doc);
    }
}