package org.codehaus.xfire.jaxb;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;
import org.codehaus.yom.stax.StaxBuilder;
import org.codehaus.yom.xpath.YOMXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * User: chris
 * Date: Aug 21, 2005
 * Time: 1:41:05 PM
 */
public class JaxbSchemaProvider
{
    private Document[] schemas;
    private Map schemaCache;

    public JaxbSchemaProvider(List schemaLocations)
    {
        schemaCache = new HashMap();
        List schemaList = new ArrayList();

        StaxBuilder builder = new StaxBuilder();
        for (Iterator iterator = schemaLocations.iterator(); iterator.hasNext();)
        {
            String s = (String) iterator.next();
            InputStream fileInputStream = null;
            try
            {
                fileInputStream = new FileInputStream(s);
            } catch (FileNotFoundException e)
            {
                fileInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(s);
            }
            if (fileInputStream == null)
                throw new XFireRuntimeException("couldnt load schema file:" + s);
            Document schema = null;
            try
            {
                schema = builder.build(fileInputStream);
            } catch (XMLStreamException e)
            {
                throw new XFireRuntimeException("error parsing schema file:" + s, e);
            }
            schemaList.add(schema);

        }
        schemas = (Document[]) schemaList.toArray(new Document[schemaList.size()]);

    }


    public Element getSchema(Type jaxbType, JaxbWsdlBuilder jaxbWsdlBuilder)
    {
        Element schema = null;


        List nodes = null;
        String ns = jaxbType.getSchemaType().getNamespaceURI();
        Element o = (Element) schemaCache.get(ns);
        if (o != null)
            return o;
        boolean found = false;
        for (int i = 0; i < schemas.length; i++)
        {

            Document document = schemas[i];
            schema = document.getRootElement();
            String expr = "//xsd:schema[@targetNamespace='" + ns + "']";

            nodes = getMatches(schema, expr);
            if (nodes.size() != 0)
            {

                found = true;
                break;
            }
        }

        if (!found)
            throw new XFireRuntimeException("couldnt find namespace " + ns);
        Element node = (Element) nodes.get(0);

        nodes = getMatches(schema, "//xsd:import");
        for (int i = 0; i < nodes.size(); i++)
        {
            Element imp = (Element) nodes.get(i);

            String importedNs = imp.getAttributeValue("namespace");

            // TODO: How do we make sure this is imported???

            imp.detach();
        }
        schemaCache.put(ns, node);
        return node;
    }

    private List getMatches(Object doc, String xpath)
    {
        try
        {
            XPath path = new YOMXPath(xpath);
            path.addNamespace("xsd", SoapConstants.XSD);
            path.addNamespace("s", SoapConstants.XSD);
            List result = path.selectNodes(doc);
            return result;
        }
        catch (JaxenException e)
        {
            throw new XFireRuntimeException("Error evaluating xpath " + xpath, e);
        }
    }
}
