package org.codehaus.xfire.spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.xmlbeans.XMLBeansWSDLBuilderAdapter;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Spring Configurer for the XMLBeansWSDLBuilder
 * 
 * @author bbonner $Id$ $Revision$
 */

public class XMLBeansWSDLBuilderConfigurer
{

    /** Logging Instance */
    private static Log log = LogFactory.getLog(XMLBeansWSDLBuilderConfigurer.class);

    private Resource[] mySchemaLocations;

    private ServiceRegistry myServiceRegistry;

    private String myService;

    private TransportManager myTransportManager;

    public void init()
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;

            builder = factory.newDocumentBuilder();

            List schemaList = new ArrayList();

            for (int i = 0; i < mySchemaLocations.length; i++)
            {
                org.w3c.dom.Document schema = builder.parse(mySchemaLocations[i].getFile());
                schemaList.add(schema);
            }
            Document[] schemas = (Document[]) schemaList.toArray(new Document[schemaList.size()]);

            Service endpoint = myServiceRegistry.getService(myService);

            endpoint.setWSDLWriter(new XMLBeansWSDLBuilderAdapter(endpoint, myTransportManager,
                    schemas));
        }
        catch (ParserConfigurationException e)
        {
            log.error("Initialization Error", e);
            throw new RuntimeException(e);
        }
        catch (SAXException e)
        {
            log.error("Initialization Error", e);
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            log.error("Initialization Error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param schema
     *            The schema to set.
     */
    public void setSchemaLocation(Resource schemaLocation)
    {
        mySchemaLocations = new Resource[] { schemaLocation };
    }

    public void setSchemaLocations(Resource[] schemaLocations)
    {
        mySchemaLocations = schemaLocations;
    }

    /**
     * @param serviceName
     *            The serviceName to set.
     */
    public void setService(String serviceName)
    {
        myService = serviceName;
    }

    /**
     * @param serviceRegistry
     *            The serviceRegistry to set.
     */
    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        myServiceRegistry = serviceRegistry;
    }

    /**
     * @param transportManager
     *            The transportManager to set.
     */
    public void setTransportManager(TransportManager transportManager)
    {
        myTransportManager = transportManager;
    }
}
