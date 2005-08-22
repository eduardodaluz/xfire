package org.codehaus.xfire.xmlbeans;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.transport.TransportManager;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XmlBeansServiceFactory
        extends ObjectServiceFactory
{
    public XmlBeansServiceFactory()
    {
        setStyle(SoapConstants.STYLE_DOCUMENT);
        setWsdlBuilderFactory(new XmlBeansWSDLBuilderFactory());
    }

    public XmlBeansServiceFactory(TransportManager transportManager)
    {
        super(transportManager);
        
        AegisBindingProvider provider = new AegisBindingProvider(new XmlBeansTypeRegistry());
        setBindingProvider(provider);
        
        setStyle(SoapConstants.STYLE_DOCUMENT);
        setWsdlBuilderFactory(new XmlBeansWSDLBuilderFactory());
    }

    protected QName getInParameterName(Service service, Method method, int paramNumber, boolean doc)
    {
        Class[] paramClasses = method.getParameterTypes();
        if (XmlObject.class.isAssignableFrom(paramClasses[paramNumber]))
        {
            return XmlBeans.typeForClass(paramClasses[paramNumber]).getDocumentElementName();
        }
        else
        {
            return super.getInParameterName(service, method, paramNumber, doc);
        }
    }

    protected QName getOutParameterName(Service service, Method method, boolean doc)
    {
        if (XmlObject.class.isAssignableFrom(method.getReturnType()))
        {
            return XmlBeans.typeForClass(method.getReturnType()).getDocumentElementName();
        }
        else
        {
            return super.getOutParameterName(service, method, doc);
        }
    }
}
