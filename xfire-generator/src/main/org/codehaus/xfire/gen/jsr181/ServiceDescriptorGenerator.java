package org.codehaus.xfire.gen.jsr181;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.xfire.gen.GenerationContext;
import org.codehaus.xfire.gen.GeneratorPlugin;
import org.codehaus.xfire.service.Service;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.sun.codemodel.JClass;

public class ServiceDescriptorGenerator
    implements GeneratorPlugin
{
    private Namespace xfireNs = 
        Namespace.getNamespace("http://xfire.codehaus.org/config/1.0");
    
    public void generate(GenerationContext context)
        throws Exception
    {
        if (!context.isServerStubGenerated()) return;
        
        File dir = new File(context.getOutputDirectory(), "META-INF/xfire/");
        File descriptor = new File(dir, "services.xml");
        
        if (!context.isDescriptorOverwritten() && descriptor.exists()) return;
        
        dir.mkdirs();
        
        Element root = new Element("beans", xfireNs); 
        Document doc = new Document(root);

        for (Iterator itr = context.getServices().entrySet().iterator(); itr.hasNext();)
        {
            Map.Entry entry = (Map.Entry) itr.next();

            generate(context, root, (QName) entry.getKey(), (List<Service>) entry.getValue());
        }
        
        Element serviceFactoryBean = context.getSchemaGenerator().getServiceFactoryBean();
        if (serviceFactoryBean != null)
        {
            root.addContent(serviceFactoryBean);
        }
        
        writeDocument(context, doc, descriptor);
    }

    public void generate(GenerationContext context, Element servicesEl, QName name, List<Service> services)
        throws Exception
    {
        for (Service service : services)
        {
            generate(context, servicesEl, service);
        }
    }
    
    public void generate(GenerationContext context, Element servicesEl, Service service)
        throws Exception
    {
        Element serviceEl = new Element("service", xfireNs);
        servicesEl.addContent(serviceEl);
        
        JClass serviceImpl = (JClass) service.getProperty(ServiceStubGenerator.SERVICE_STUB);
        if (serviceImpl == null) return;
        
        Element serviceClassEl = new Element("serviceClass", xfireNs);
        serviceClassEl.addContent(serviceImpl.fullName());
        serviceEl.addContent(serviceClassEl);
        
        Element wsdlEl = new Element("wsdlURL", xfireNs);
        wsdlEl.addContent(context.getWsdlLocation());
        serviceEl.addContent(wsdlEl);
        
        Element serviceFactoryEl = new Element("serviceFactory", xfireNs);
        serviceFactoryEl.addContent(context.getSchemaGenerator().getServiceFactory());
        serviceEl.addContent(serviceFactoryEl);
    }
    
    private void writeDocument(GenerationContext context, Document doc, File location) throws Exception
    {
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        
        location.createNewFile();
        outputter.output(doc, new FileOutputStream(location));
    }
}
