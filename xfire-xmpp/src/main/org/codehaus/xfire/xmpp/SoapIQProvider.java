package org.codehaus.xfire.xmpp;

import java.io.IOException;
import java.util.Iterator;

import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.soap.SoapVersionFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

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
        try
        {
            return new SoapEnvelopePacket(parseDocument(parser));
        }
        catch(Exception e)
        {
            // TODO: Add better debugging
            e.printStackTrace();
            throw e;
        }
    }

    protected Document parseDocument(XmlPullParser pp)
        throws DocumentException, IOException, XmlPullParserException
    {
        DocumentFactory df = DocumentFactory.getInstance();
        Document document = df.createDocument();
        Element parent = null;

        int depth = pp.getDepth();
        int type = pp.getEventType();
        
        while (true)
        {
            switch (type)
            {
            case XmlPullParser.PROCESSING_INSTRUCTION:
            {
                String text = pp.getText();
                int loc = text.indexOf(" ");

                if (loc >= 0)
                {
                    String target = text.substring(0, loc);
                    String txt = text.substring(loc + 1);
                    document.addProcessingInstruction(target, txt);
                }
                else
                {
                    document.addProcessingInstruction(text, "");
                }

                break;
            }

            case XmlPullParser.COMMENT:
            {
                if (parent != null)
                {
                    parent.addComment(pp.getText());
                }
                else
                {
                    document.addComment(pp.getText());
                }

                break;
            }

            case XmlPullParser.CDSECT:
            {
                if (parent != null)
                {
                    parent.addCDATA(pp.getText());
                }
                else
                {
                    String msg = "Cannot have text content outside of the " + "root document";
                    throw new DocumentException(msg);
                }

                break;
            }

            case XmlPullParser.ENTITY_REF:
                break;

            case XmlPullParser.END_DOCUMENT:
                return document;

            case XmlPullParser.START_TAG:
            {
                QName qname = (pp.getPrefix() == null) ? df.createQName(pp.getName(), pp
                        .getNamespace()) : df.createQName(pp.getName(), pp.getPrefix(), pp
                        .getNamespace());
                Element newElement = df.createElement(qname);
                int nsStart = pp.getNamespaceCount(pp.getDepth() - 1);
                int nsEnd = pp.getNamespaceCount(pp.getDepth());

                for (int i = nsStart; i < nsEnd; i++)
                {
                    if (pp.getNamespacePrefix(i) != null)
                    {
                        newElement.addNamespace(pp.getNamespacePrefix(i), pp.getNamespaceUri(i));
                    }
                }

                for (int i = 0; i < pp.getAttributeCount(); i++)
                {
                    QName qa = (pp.getAttributePrefix(i) == null) ? df.createQName(pp
                            .getAttributeName(i)) : df.createQName(pp.getAttributeName(i), pp
                            .getAttributePrefix(i), pp.getAttributeNamespace(i));
                    newElement.addAttribute(qa, pp.getAttributeValue(i));
                }

                if (parent != null)
                {
                    parent.add(newElement);
                }
                else
                {
                    document.add(newElement);
                }

                parent = newElement;

                break;
            }

            case XmlPullParser.END_TAG:
            {
                if (parent != null)
                {
                    parent = parent.getParent();
                }

                if (pp.getDepth() <= depth 
                    && parent == null)
                {
                    return document;
                }
                break;
            }

            case XmlPullParser.TEXT:
            {
                String text = pp.getText();
                if (parent != null)
                {
                    parent.addText(text);
                }
                else
                {
                    String msg = "Cannot have text content outside of the " + "root document";
                    throw new DocumentException(msg);
                }

                break;
            }

            default:
                break;
            }
            
            type = pp.nextToken();
        }
    }
}
