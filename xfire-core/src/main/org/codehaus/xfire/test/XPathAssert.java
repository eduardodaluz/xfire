package org.codehaus.xfire.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.XPath;

/**
 * WebService assertions.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XPathAssert
{
    /**
     * Assert that the following XPath query selects one or more nodes.
     * 
     * @param xpath
     */
    public static void assertValid(String xpath, Node node, Map namespaces)
        throws Exception
    {
        List nodes = createXPath(xpath, namespaces).selectNodes(node);

        if (nodes.size() == 0)
        {
            throw new Exception("Failed to select any nodes for expression:.\n" + xpath + "\n"
                    + node.asXML());
        }
    }

    /**
     * Assert that the following XPath query selects no nodes.
     * 
     * @param xpath
     */
    public static void assertInvalid(String xpath, Node node, Map namespaces)
        throws Exception
    {
        List nodes = createXPath(xpath, namespaces).selectNodes(node);

        if (nodes.size() > 0)
        {
            throw new Exception("Found multiple nodes for expression:\n" + xpath + "\n"
                    + node.asXML());
        }
    }

    /**
     * Asser that the text of the xpath node retrieved is equal to the value
     * specified.
     * 
     * @param xpath
     * @param value
     * @param node
     */
    public static void assertXPathEquals(String xpath, String value, Node node, Map namespaces)
        throws Exception
    {
        String value2 = createXPath(xpath, namespaces).selectSingleNode(node).getText().trim();

        Assert.assertEquals(value, value2);
    }

    public static void assertNoFault(Node node)
        throws Exception
    {
        Map namespaces = new HashMap();
        namespaces.put("s", Soap11.getInstance().getNamespace());
        namespaces.put("s12", Soap12.getInstance().getNamespace());
        assertInvalid("/s:Envelope/s:Body/s:Fault", node, namespaces);
        assertInvalid("/s12:Envelope/s12Body/s12:Fault", node, namespaces);
    }

    /**
     * Create the specified XPath expression with the namespaces added via
     * addNamespace().
     */
    protected static XPath createXPath(String xpathString, Map namespaces)
    {
        XPath xpath = DocumentHelper.createXPath(xpathString);
        xpath.setNamespaceURIs(namespaces);

        return xpath;
    }
}
