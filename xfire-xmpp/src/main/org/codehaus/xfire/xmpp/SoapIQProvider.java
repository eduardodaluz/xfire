package org.codehaus.xfire.xmpp;

import java.util.Iterator;

import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.soap.SoapVersionFactory;
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.codehaus.xfire.util.stax.FragmentStreamReader;
import org.jdom.Document;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class SoapIQProvider
    implements IQProvider
{
    static
    {
        /* Register an IQ Provider for each soap namespace */
        SoapIQProvider provider = new SoapIQProvider();

        for (Iterator itr = SoapVersionFactory.getInstance().getVersions(); itr.hasNext();)
        {
            SoapVersion version = (SoapVersion) itr.next();
            
            ProviderManager.addIQProvider(version.getEnvelope().getLocalPart(), 
                                          version.getEnvelope().getNamespaceURI(),
                                          provider);
        }
    }

    /**
     * @param parser
     * @return
     * @throws java.lang.Exception
     */
    public IQ parseIQ(XmlPullParser parser)
        throws Exception
    {
        StaxBuilder builder = new StaxBuilder();
        FragmentStreamReader reader = new FragmentStreamReader(new XmlPullStreamReader(parser));
        reader.setAdvanceAtEnd(false);
        
        Document doc = builder.build(reader);
 
        return new SoapEnvelopePacket(doc);
    }
}
