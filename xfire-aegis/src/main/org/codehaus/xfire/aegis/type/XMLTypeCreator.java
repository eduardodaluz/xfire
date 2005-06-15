package org.codehaus.xfire.aegis.type;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;
import java.beans.PropertyDescriptor;

import javax.xml.stream.XMLStreamException;

import org.codehaus.yom.Document;
import org.codehaus.yom.Element;
import org.codehaus.yom.xpath.YOMXPath;
import org.codehaus.yom.stax.StaxBuilder;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.XPath;
import org.jaxen.JaxenException;

/**
 * Deduce mapping information from an xml file.
 * The xml file should be in the same packages as the class, with the name <code>className.aegis.xml</code>.
 * For example, given the following service interface:
 * <p/>
 * <pre>
 * public Collection getResultsForValues(String id, Collection values); //method 1
 * public Collection getResultsForValues(int id, Collection values); //method 2
 * public String getResultForValue(String value); //method 3
 * </pre>
 * An example of the type xml is:
 * <pre>
 * &lt;mappings&gt;
 *  &lt;mapping&gt;
 *    &lt;method name="getResultsForValues"&gt;
 *      &lt;return-type componentType="com.acme.ResultBean" /&gt;
 *      &lt;!-- no need to specify index 0, since it's a String --&gt;
 *      &lt;parameter index="1" componentType="java.lang.String" /&gt;
 *    &lt;/method&gt;
 *  &lt;/mapping&gt;
 * &lt;/mappings&gt;
 * </pre>
 * <p/>
 * Note that for values which can be easily deduced (such as the String parameter, or the second service method)
 * no mapping need be specified in the xml descriptor, which is why no mapping is specified for method 3.
 * <p/>
 * However, if you have overloaded methods with different semantics, then you will need to specify enough
 * parameters to disambiguate the method and uniquely identify it. So in the example above, the mapping
 * specifies will apply to both method 1 and method 2, since the parameter at index 0 is not specified.
 *
 * @author Hani Suleiman
 *         Date: Jun 14, 2005
 *         Time: 7:47:56 PM
 */
public class XMLTypeCreator extends AbstractTypeCreator
{
    private static final Log log = LogFactory.getLog(XMLTypeCreator.class);
    //cache of classes to documents
    private Map documents = new HashMap();

    protected Document getDocument(Class clazz)
    {
        Document doc = (Document)documents.get(clazz.getName());
        if(doc != null)
        {
            return doc;
        }
        String path = '/' + clazz.getName().replace('.', '/') + ".aegis.xml";
        InputStream is = clazz.getResourceAsStream(path);
        if(is == null) return null;
        try
        {
            doc = new StaxBuilder().build(is);
            documents.put(clazz.getName(), doc);
            return doc;
        }
        catch(XMLStreamException e)
        {
            log.error("Error loading file " + path, e);
        }
        return null;
    }

    public Type createCollectionType(TypeClassInfo info)
    {
        return super.createCollectionType(info, (Class)info.getGenericType());
    }

    public TypeClassInfo createClassInfo(PropertyDescriptor pd)
    {
        Method m = pd.getReadMethod();
        return createClassInfo(m, -1);
    }

    public Type createDefaultType(TypeClassInfo info)
    {
        return nextCreator.createDefaultType(info);
    }

    public TypeClassInfo createClassInfo(Method m, int index)
    {
        Document doc = getDocument(m.getDeclaringClass());
        if(doc == null) return nextCreator.createClassInfo(m, index);
        //find the elements that apply to the specified method
        TypeClassInfo info = new TypeClassInfo();
        if(index >= 0)
        {
            //we don't want nodes for which the specified index is not specified
            List nodes = getMatches(doc, "//mappings/mapping/method[@name='" + m.getName() + "']/parameter[@index='" + index + "']/parent::*");
            if(nodes.size() == 0)
            {
                //no mapping for this method
                return nextCreator.createClassInfo(m, index);
            }
            //pick the best matching node
            Element bestMatch = getBestMatch(doc, m, nodes);
            if(bestMatch == null)
            {
                //no mapping for this method
                return nextCreator.createClassInfo(m, index);
            }
            info.setTypeClass(m.getParameterTypes()[index]);
            //info.setAnnotations(m.getParameterAnnotations()[index]);
            Element parameter = getMatch(bestMatch, "parameter[@index='" + index + "']");
            String componentType = parameter.getAttributeValue("componentType");
            if(componentType != null)
            {
                try
                {
                    info.setGenericType(ClassLoaderUtils.loadClass(componentType, getClass()));
                }
                catch(ClassNotFoundException e)
                {
                    log.error("Unable to load mapping class " + componentType);
                }
            }
        }
        else
        {
            List nodes = getMatches(doc, "//mappings/mapping/method[@name='" + m.getName() + "']/return-type");
            if(nodes.size() == 0) return nextCreator.createClassInfo(m, index);
            info.setTypeClass(m.getReturnType());
            //info.setAnnotations(m.getAnnotations());
            String componentType = ((Element)nodes.get(0)).getAttributeValue("componentType");
            if(componentType != null)
            {
                try
                {
                    info.setGenericType(ClassLoaderUtils.loadClass(componentType, getClass()));
                }
                catch(ClassNotFoundException e)
                {
                    log.error("Unable to load mapping class " + componentType);
                }
            }
        }

        return info;
    }

    private Element getBestMatch(Document doc, Method method, List availableNodes)
    {
        //first find all the matching method names
        List nodes = getMatches(doc, "//mappings/mapping/method[@name='" + method.getName() + "']");
        //remove the ones that aren't in our acceptable set, if one is specified
        if(availableNodes != null)
        {
            nodes.retainAll(availableNodes);
        }
        //no name found, so no matches
        if(nodes.size() == 0) return null;
        //if the method has no params, then more than one mapping is pointless
        Class[] parameterTypes = method.getParameterTypes();
        if(parameterTypes.length == 0) return (Element)nodes.get(0);
        //here's the fun part.
        //we go through the method parameters, ruling out matches
        for(int i = 0; i < parameterTypes.length; i++)
        {
            Class parameterType = parameterTypes[i];
            for(Iterator iterator = nodes.iterator(); iterator.hasNext();)
            {
                Element element = (Element)iterator.next();
                //first we check if the parameter index is specified
                Element match = getMatch(element, "/parameter[@index='" + i + "']");
                if(match != null)
                {
                    //we check if the type is specified and matches
                    if(match.getAttributeValue("type") != null)
                    {
                        //if it doesn't match, then we can definitely rule out this result
                        if(!match.getAttributeValue("type").equals(parameterType.getName()))
                        {
                            iterator.remove();
                        }
                    }
                }
            }
        }
        //all remaining definitions could apply, so we need to now pick the best one
        //the best one is the one with the most parameters specified
        Element bestCandidate = null;
        int highestSpecified = 0;
        for(Iterator iterator = nodes.iterator(); iterator.hasNext();)
        {
            Element element = (Element)iterator.next();
            int availableParameters = element.getChildElements("parameter").size();
            if(availableParameters > highestSpecified)
            {
                bestCandidate = element;
                highestSpecified = availableParameters;
            }
        }
        return bestCandidate;
    }

    private Element getMatch(Object doc, String xpath)
    {
        try
        {
            XPath path = new YOMXPath(xpath);
            return (Element)path.selectSingleNode(doc);
        }
        catch(JaxenException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private List getMatches(Object doc, String xpath)
    {
        try
        {
            XPath path = new YOMXPath(xpath);
            List result = path.selectNodes(doc);
            return result;
        }
        catch(JaxenException e)
        {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }
}
