package org.codehaus.xfire.xmlbeans.generator;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.xml.sax.EntityResolver;
import org.xmlsoap.schemas.wsdl.DefinitionsDocument;
import org.xmlsoap.schemas.wsdl.TBinding;
import org.xmlsoap.schemas.wsdl.TBindingOperation;
import org.xmlsoap.schemas.wsdl.TDefinitions;
import org.xmlsoap.schemas.wsdl.TMessage;
import org.xmlsoap.schemas.wsdl.TOperation;
import org.xmlsoap.schemas.wsdl.TParam;
import org.xmlsoap.schemas.wsdl.TPart;
import org.xmlsoap.schemas.wsdl.TPortType;
import org.xmlsoap.schemas.wsdl.TService;

/**
 * Inspects WSDL documents and provide handy classes to examine the 
 * content.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 27, 2004
 */
public class WSDLInspector
{
    public final static String schemaNS = 
        "declare namespace xs=\"http://www.w3.org/2001/XMLSchema\"";
    public final static String wsdlNS = 
        "declare namespace wsdl=\"http://schemas.xmlsoap.org/wsdl/\"";
    public final static String wsdlSoapNS = 
        "declare namespace soap=\"http://schemas.xmlsoap.org/wsdl/soap/\"";
    public final static String httpNS = 
        "declare namespace http=\"http://schemas.xmlsoap.org/wsdl/http/";
    
    SchemaTypeLoader loader = 
        XmlBeans.typeLoaderForClassLoader(XmlObject.class.getClassLoader());
    
    public List generateServices(URL document) throws Exception
    {
        EntityResolver entResolver = null;

        XmlOptions options = new XmlOptions();
        options.setLoadLineNumbers();

        options.setEntityResolver(entResolver);

        SchemaTypeLoader wloader = 
            XmlBeans.typeLoaderForClassLoader(TDefinitions.class.getClassLoader());
        XmlObject wsdldoc = wloader.parse(document, null, options);
        
        TDefinitions defs = ((DefinitionsDocument)wsdldoc).getDefinitions();
        XmlObject[] types = defs.getTypesArray();
        
        ArrayList services = new ArrayList();
        
        TService[] xServices = defs.getServiceArray();
        for ( int i = 0; i < xServices.length; i++ )
        {
            TService xService = xServices[i];
            
            Service service = new Service();
            
            XmlObject[] xAddresses = xService.selectPath(wsdlSoapNS + " $this//soap:address");
            if (xAddresses == null || xAddresses.length == 0)
            {
                xAddresses = xService.selectPath(httpNS + " $this//http:address");
                if (xAddresses == null || xAddresses.length == 0)
                    break;
                
                service.setRest(true);
                break;
            }

            service.setName(xService.getName());
            service.setUrl(xAddresses[0].selectAttribute("","location").newCursor().getTextValue());
            service.setXmlObject(xService);
            service.setBinding(xService.getPortArray(0).getBinding());

            TBinding xBinding = getBinding( defs, service.getBinding() );
            if ( xBinding  == null )
                throw new RuntimeException("Couldn't find binding!");
            
            service.setPortType(xBinding.getType());
            
            TPortType xPortType = getPortType( defs, service.getPortType() );
            if ( xPortType  == null )
                throw new RuntimeException("Couldn't find port type!");
            
            TBindingOperation[] xOperations = xBinding.getOperationArray();
            
            for ( int j = 0; j < xOperations.length; j++ )
            {
                ServiceMethod m = createMethod( xOperations[j], xPortType, defs );
                
                service.addMethod(m);
            }
            
            services.add(service);
        }
        
        return services;
    }

    private ServiceMethod createMethod(TBindingOperation xOperation, TPortType portType, TDefinitions defs)
    {
        ServiceMethod m = new ServiceMethod();
        m.setName(xOperation.getName());
        
        TOperation abstractOp = getAbstractOperation( m.getName(), portType );
        TParam input = abstractOp.getInput();
        TMessage message = getMessage( input.getMessage().getLocalPart(), defs );

        TPart[] xParts = message.getPartArray();
        for ( int i = 0; i < xParts.length; i++ )
        {
            Parameter p = new Parameter();
            p.setName(xParts[i].getName());
            SchemaType type = loader.findDocumentType(xParts[i].getElement());
            if ( type == null )
            {
                System.out.println("Couldn't find type " + xParts[i].getElement().toString());
                type = XmlAnySimpleType.type;
            }
            p.setType( type );
            
            m.addRequestParameter(p);
        }
        
        XmlObject[] xHeaders = xOperation.getInput().selectPath(wsdlSoapNS + " $this//soap:header");
        if ( xHeaders != null )
        {
            QName partQ = new QName("part");
            QName messageQ = new QName("message");
            
            for ( int i = 0; i < xHeaders.length; i++ )
            {
                String msgName = xHeaders[i].newCursor().getAttributeText(messageQ);
                msgName = msgName.substring(msgName.indexOf(":")+1);
                
                String partName = xHeaders[i].newCursor().getAttributeText(partQ);
                TMessage hMessage = getMessage( msgName, defs );
                TPart part = getPart(hMessage, partName);
                
                Parameter p = new Parameter();
                p.setName(part.getName() + "Header");
                SchemaType type = loader.findDocumentType(part.getElement());
                if ( type == null )
                {
                    System.out.println("Couldn't find type " + xParts[i].getElement().toString());
                    type = XmlAnySimpleType.type;
                }
                p.setType( type );
                
                m.addRequestHeader(p);
            }
        }
        
        TParam output = abstractOp.getOutput();
        message = getMessage( output.getMessage().getLocalPart(), defs );
        
        xParts = message.getPartArray();
        for ( int i = 0; i < xParts.length; i++ )
        {
            Parameter p = new Parameter();
            p.setName(xParts[i].getName());
            p.setType( loader.findDocumentType(xParts[i].getElement()) );
            
            m.addResponseParameter(p);
        }
        
        // todo get soap action
        return m;
    }

    private TPart getPart(TMessage message, String partName)
    {
        TPart[] parts = message.getPartArray();
        for ( int i = 0; i < parts.length; i ++ )
        {
            if ( parts[i].getName().equals(partName) )
                return parts[i];
        }
        return null;
    }

    private TMessage getMessage(String name, TDefinitions defs)
    {
        TMessage[] xMessages = defs.getMessageArray();
        for ( int i = 0; i < xMessages.length; i ++ )
        {
            if ( xMessages[i].getName().equals(name) )
                return xMessages[i];
        }
        return null;
    }

    private TOperation getAbstractOperation(String name, TPortType portType)
    {
        TOperation[] xOperations = portType.getOperationArray();
        
        for ( int j = 0; j < xOperations.length; j++ )
        {
            if ( xOperations[j].getName().equals(name) )
                return xOperations[j];
        }
        return null;
    }

    private TPortType getPortType(TDefinitions defs, QName portType)
    {
        TPortType[] portTypes = defs.getPortTypeArray();
        for ( int i = 0; i < portTypes.length; i++ )
        {
            if ( portTypes[i].getName().equals(portType.getLocalPart()) )
                return portTypes[i];
        }
        
        return null;
    }

    private TBinding getBinding(TDefinitions defs, QName binding)
    {
        TBinding[] bindings = defs.getBindingArray();
        for ( int i = 0; i < bindings.length; i++ )
        {
            if ( bindings[i].getName().equals(binding.getLocalPart()) )
                return bindings[i];
        }
        
        return null;
    }

    public class Service
    {
        private String name;
        private String url;
        private List methods;
        private String encoding = "UTF-8";
        private XmlObject xmlObject;
        private QName binding;
        private QName portType;
        private boolean isRest;
        private String soapVersion;
        
        public Set getImports()
        {
            Set imports = new HashSet();
            
            if ( methods != null )
            {
                for ( Iterator itr = methods.iterator(); itr.hasNext(); )
                {
                    ServiceMethod m = (ServiceMethod) itr.next();
                    
                    if ( m.getRequestParameters() != null )
                    {
                        for ( Iterator pitr = m.getRequestParameters().iterator(); pitr.hasNext(); )
                        {
                            Parameter p = (Parameter) pitr.next();
                            imports.add( p.getType() );
                        }
                    }
                    
                    if ( m.getResponseParameters() != null )
                    {
                        for ( Iterator pitr = m.getResponseParameters().iterator(); pitr.hasNext(); )
                        {
                            Parameter p = (Parameter) pitr.next();
                            imports.add( p.getType() );
                        }
                    }
                    
                    if ( m.getRequestHeaders() != null )
                    {
                        for ( Iterator pitr = m.getRequestHeaders().iterator(); pitr.hasNext(); )
                        {
                            Parameter p = (Parameter) pitr.next();
                            imports.add( p.getType() );
                        }
                    }
                }
            }
            return imports;
        }

        public String getSoapVersion()
        {
            return soapVersion;
        }
        public void setSoapVersion(String soapVersion)
        {
            this.soapVersion = soapVersion;
        }
        public boolean isRest()
        {
            return isRest;
        }
        public void setRest(boolean isRest)
        {
            this.isRest = isRest;
        }
        public QName getPortType()
        {
            return portType;
        }
        public void setPortType(QName portType)
        {
            this.portType = portType;
        }
        public QName getBinding()
        {
            return binding;
        }
        public void setBinding(QName binding)
        {
            this.binding = binding;
        }
        public XmlObject getXmlObject()
        {
            return xmlObject;
        }
        public void setXmlObject(XmlObject xmlObject)
        {
            this.xmlObject = xmlObject;
        }
        public void addMethod( ServiceMethod m )
        {
            if ( methods == null )
                methods = new ArrayList();
            
            methods.add(m);
        }
        
        public String getEncoding()
        {
            return encoding;
        }
        public void setEncoding(String encoding)
        {
            this.encoding = encoding;
        }
        public List getMethods()
        {
            return methods;
        }
        public void setMethods(List methods)
        {
            this.methods = methods;
        }
        public String getName()
        {
            return name;
        }
        public void setName(String name)
        {
            this.name = name;
        }
        public String getUrl()
        {
            return url;
        }
        public void setUrl(String url)
        {
            this.url = url;
        }
    }
    
    public class ServiceMethod
    {
        private String soapAction;
        private String name;
        private List requestParameters;
        private List responseParameters;
        private List requestHeaders;
        private List responseHeaders;
        private XmlObject xmlObject;

        public void addRequestParameter( Parameter parameter )
        {
            if ( requestParameters == null )
                requestParameters = new ArrayList();
            
            requestParameters.add(parameter);
        }
        public void addResponseParameter( Parameter parameter )
        {
            if ( responseParameters == null )
                responseParameters = new ArrayList();
            
            responseParameters.add(parameter);
        }
        
        public void addRequestHeader( Parameter parameter )
        {
            if ( requestHeaders == null )
                requestHeaders = new ArrayList();
            
            requestHeaders.add(parameter);
        }
        
        public void addResponseHeader( Parameter parameter )
        {
            if ( responseHeaders == null )
                responseHeaders = new ArrayList();
            
            responseHeaders.add(parameter);
        }
        
        public List getRequestParameters()
        {
            return requestParameters;
        }
        public void setRequestParameters(List requestParameters)
        {
            this.requestParameters = requestParameters;
        }
        public List getResponseParameters()
        {
            return responseParameters;
        }
        public void setResponseParameters(List responseParameters)
        {
            this.responseParameters = responseParameters;
        }
        public List getRequestHeaders()
        {
            return requestHeaders;
        }
        public void setRequestHeaders(List requestHeaders)
        {
            this.requestHeaders = requestHeaders;
        }
        public List getResponseHeaders()
        {
            return responseHeaders;
        }
        public void setResponseHeaders(List responseHeaders)
        {
            this.responseHeaders = responseHeaders;
        }
        public String getSoapAction()
        {
            return soapAction;
        }
        public XmlObject getXmlObject()
        {
            return xmlObject;
        }
        public void setXmlObject(XmlObject xmlObject)
        {
            this.xmlObject = xmlObject;
        }
        public String getName()
        {
            return name;
        }
        public void setName(String name)
        {
            this.name = name;
        }
        public void setSoapAction(String soapAction)
        {
            this.soapAction = soapAction;
        }
    }
    
    public class Parameter
    {
        private SchemaType type;
        private String name;
        
        public String getName()
        {
            return name;
        }
        public void setName(String name)
        {
            this.name = name;
        }
        public SchemaType getType()
        {
            return type;
        }
        public void setType(SchemaType type)
        {
            this.type = type;
        }
    }
}
