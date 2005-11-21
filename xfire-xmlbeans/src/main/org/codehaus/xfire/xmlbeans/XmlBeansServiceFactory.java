package org.codehaus.xfire.xmlbeans;

import org.codehaus.xfire.aegis.AegisBindingProvider;
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
        AegisBindingProvider provider = new AegisBindingProvider(new XmlBeansTypeRegistry());
        setBindingProvider(provider);
        
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
}
