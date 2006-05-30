package org.codehaus.xfire.jaxb2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.w3c.dom.Document;

public class JaxbWSDLBuilder
    extends WSDLBuilder
{
    private Set<Class> classes = new HashSet<Class>();
    private Set<JaxbType> types = new HashSet<JaxbType>();
    private Set<String> namespaces = new HashSet<String>();
    
    public JaxbWSDLBuilder(Service service, TransportManager transportManager) throws WSDLException
    {
        super(service, transportManager);
    }

    public void addDependency(org.codehaus.xfire.wsdl.SchemaType type)
    {
        if (!hasDependency(type))
        {
            if (type instanceof JaxbType)
            {
                JaxbType jaxbType = (JaxbType) type;

                if (types.contains(jaxbType)) return;
                
                classes.add(jaxbType.getTypeClass());
                namespaces.add(jaxbType.getSchemaType().getNamespaceURI());
                types.add(jaxbType);
            }
        }
        else
        {
            super.addDependency(type);
        }
    }
    
    
    @Override
    protected void writeComplexTypes()
        throws WSDLException
    {
        try
        {
            JAXBContext context = JAXBContext.newInstance(classes.toArray(new Class[0]));
            final List<DOMResult> results = new ArrayList<DOMResult>();
            
            for (String ns : namespaces)
            {
                context.generateSchema(new SchemaOutputResolver() {
                    @Override
                    public Result createOutput(String ns, String file)
                        throws IOException
                    {
                        DOMResult result = new DOMResult();
                        result.setSystemId(file);
                        
                        results.add(result);
                        
                        return result;
                    }
                });
            }
            
            Types types = getDefinition().getTypes();
            if (types == null)
            {
                types = getDefinition().createTypes();
                getDefinition().setTypes(types);
            }
            
            DOMBuilder domBuilder = new DOMBuilder();
            
            for (DOMResult result : results)
            {
                Element schema = domBuilder.build(((Document)result.getNode()).getDocumentElement());
                schema.detach();
                getSchemaTypes().addContent(schema);
            }
        }
        catch (JAXBException e)
        {
            throw new XFireRuntimeException("Couldn't generate a schema for the JAXB objects!", e);
        }
        catch (IOException e)
        {
            throw new XFireRuntimeException("Couldn't generate a schema for the JAXB objects!", e);
        }
        
        super.writeComplexTypes();
    }
    
}